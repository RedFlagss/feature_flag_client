package org.redflag.configuration;

import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.redflag.dto.UsernamePasswordCredentials;
import org.redflag.service.ServerRequestsService;
import org.redflag.service.impl.ServerRequestsServiceImp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppConfig {
    private static AppConfig instance;
    @Getter
    private final KafkaConfig kafkaConfig;

    @Getter
    private String username;
    private String password;

    private AppConfig() {
        this.kafkaConfig = KafkaConfig.getInstance();
        loadCredentials();
        ServerRequestsService serverRequestsService = new ServerRequestsServiceImp(username);

        serverRequestsService.authenticateSDK(new UsernamePasswordCredentials(username, password));
        serverRequestsService.getNodeIdAndOrganizationId();
    }

    private void loadCredentials() {
        Properties props = new Properties();
        ClassLoader appClassLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream input = appClassLoader.getResourceAsStream("ff-lib.properties")) {

            if (input == null) {
                throw new RuntimeException("Internal config ff-lib.properties not found");
            }

            props.load(input);

            this.username = props.getProperty("username");
            this.password = props.getProperty("password");

        } catch (IOException e) {
            throw  new RuntimeException("Error occurred in ff-lib.properties", e);
        }
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }

        return instance;
    }

}
