package org.redflag.server;

import org.redflag.configuration.AppConfig;
import org.redflag.dto.CreateFeatureFlagRequest;
import org.redflag.dto.FeatureFlagDTO;
import org.redflag.dto.FeatureFlagsResponse;
import org.redflag.service.ServerRequestsService;
import org.redflag.service.impl.KafkaFeatureFlagConsumer;
import org.redflag.service.impl.ServerRequestsServiceImp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DataStorage {
    private final Map<String, Boolean> ffStorage;

    private static DataStorage instance;

    private AppConfig appConfig;

    private final ServerRequestsService serverRequestsService;
    private final KafkaFeatureFlagConsumer kafkaConsumer;

    public static DataStorage getInstance(AppConfig appConfig) {
        if (instance == null) {
            instance = new DataStorage(appConfig);
        }

        return instance;
    }

    private DataStorage(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.serverRequestsService = new ServerRequestsServiceImp(appConfig.getUsername());
        this.ffStorage = serverRequestsService.getAllFeatureFlags();
        this.kafkaConsumer = new KafkaFeatureFlagConsumer(appConfig);
        this.kafkaConsumer.startConsuming();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public boolean getValue(String flagKey) {
        Boolean kafkaValue = kafkaConsumer.getFlagValue(flagKey);
        if (ffStorage.putIfAbsent(flagKey, false) == null) {
            System.out.println("Flag not found: " + flagKey + " Creating flag.");
            serverRequestsService.createFeatureFlag(new CreateFeatureFlagRequest(flagKey, false));
        };

        ffStorage.put(flagKey, kafkaValue);
        return ffStorage.get(flagKey);
    }

    private void shutdown() {
        System.out.println("Shutting down DataStorage...");
        kafkaConsumer.shutdown();
    }
}
