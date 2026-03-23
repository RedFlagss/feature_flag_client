package org.redflag.server;

import org.redflag.configuration.AppConfig;
import org.redflag.service.ServerRequestsService;
import org.redflag.service.impl.KafkaFeatureFlagConsumer;
import org.redflag.service.impl.ServerRequestsServiceImp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DataStorage {
    private final Map<String, Boolean> ffStorage;
    private AppConfig appConfig;

    private static DataStorage instance;

    private final ServerRequestsService serverRequestsService;
    private final KafkaFeatureFlagConsumer kafkaConsumer;

    public static DataStorage getInstance(AppConfig appConfig) {
        if (instance == null) {
            instance = new DataStorage(appConfig);
        }

        return instance;
    }

    private DataStorage(AppConfig appConfig) {
        this.ffStorage = collectFlags();
        this.appConfig = appConfig;
        this.serverRequestsService = new ServerRequestsServiceImp();
        this.kafkaConsumer = new KafkaFeatureFlagConsumer(appConfig);

        this.kafkaConsumer.startConsuming();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public boolean getValue(String flagKey) {
        Boolean kafkaValue = kafkaConsumer.getFlagValue(flagKey);
        if (ffStorage.putIfAbsent(flagKey, false) == null) {
            System.out.println("Flag not found: " + flagKey + " Creating flag.");
            serverRequestsService.createFeatureFlag();
        };

        ffStorage.put(flagKey, kafkaValue);
        return ffStorage.get(flagKey);
    }

    private HashMap<String, Boolean> collectFlags() {
        //TODO: replace with call to kafka topic / or add call to kafka topic

        HashMap<String, Boolean> ffMap = new HashMap<>();

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("feature-flags.properties")) {

            if (input == null) {
                System.err.println("feature-flags.properties not found");
                return null;
            }

            Properties props = new Properties();
            props.load(input);

            for (String key : props.stringPropertyNames()) {
                boolean value = Boolean.parseBoolean(props.getProperty(key).trim());
                ffMap.put(key, value);
            }

        } catch (IOException e) {
            throw  new RuntimeException("Error occurred in feature-flags.properties", e);
        }

        return ffMap;
    }

    private void shutdown() {
        System.out.println("Shutting down DataStorage...");
        kafkaConsumer.shutdown();
    }
}
