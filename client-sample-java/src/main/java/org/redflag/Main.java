package org.redflag;

import org.redflag.service.FeatureFlagService;

public class Main {
    public static void main(String[] args) {
        FeatureFlagService featureFlagService = FeatureFlagService.builder()
                .backendUrl("http://localhost:8080")
                .sdkLogin("login")
                .sdkPassword("password")
                .build();


        if (featureFlagService.isEnabled("flag")) {
            System.out.println("Feature Flag Client is enabled");
        }

        if (!featureFlagService.isEnabled("flag2")) {
            System.out.println("Feature Flag 2 Client is disabled");
        }
    }
}