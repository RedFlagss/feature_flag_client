package org.redflag.configuration;

import lombok.Getter;
import org.redflag.dto.UsernamePasswordCredentials;
import org.redflag.service.ServerRequestsService;
import org.redflag.service.impl.ServerRequestsServiceImp;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    @Getter
    private final String backendUrl;
    @Getter
    private final String sdkLogin;
    @Getter
    private final String sdkPassword;
    @Getter
    private static AppConfig instance;
    @Getter
    private final List<String> kafkaTopics;

    private final ServerRequestsService serverRequestsService;

    private AppConfig(String backendUrl, String sdkLogin, String sdkPassword) {
        this.backendUrl = backendUrl;
        this.sdkLogin = sdkLogin;
        this.sdkPassword = sdkPassword;
        serverRequestsService = new ServerRequestsServiceImp();

        serverRequestsService.authenticateSDK(new UsernamePasswordCredentials(sdkLogin, sdkPassword));

        kafkaTopics = serverRequestsService.getFeatureFlagTopics().getTopics();
    }

    public static AppConfig createInstance(String backendUrl, String sdkLogin, String sdkPassword) {
        if (instance == null) {
            instance = new AppConfig(backendUrl, sdkLogin, sdkPassword);
        }

        return instance;
    }

}
