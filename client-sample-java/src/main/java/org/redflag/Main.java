package org.redflag;

import org.redflag.configuration.AppConfig;
import org.redflag.server.DataStorage;
import org.redflag.service.FeatureFlagService;
import org.redflag.service.impl.FeatureFlagServiceImp;

public class Main {
    public static void main(String[] args) {
        AppConfig appConfig = AppConfig.createInstance(
                        "/",
                        "login",
                        "password"
        );
        DataStorage dataStorage = DataStorage.getInstance(appConfig);

        FeatureFlagService featureFlagService = new FeatureFlagServiceImp(dataStorage);

        if (featureFlagService.isEnabled("flag")) {
            System.out.println("Feature Flag is enabled");
        }

        if (!featureFlagService.isEnabled("flag2")) {
            System.out.println("Feature Flag 2 is disabled");
        }

        System.out.println("FF3 " + featureFlagService.isEnabled("ff3"));
    }
}