package org.redflag;

import org.redflag.configuration.AppConfig;
import org.redflag.server.DataStorage;
import org.redflag.service.FeatureFlagService;
import org.redflag.service.impl.FeatureFlagServiceImp;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        AppConfig appConfig = AppConfig.getInstance();
        DataStorage dataStorage = DataStorage.getInstance(appConfig);

        FeatureFlagService featureFlagService = new FeatureFlagServiceImp(dataStorage);

        while (true) {
            if (featureFlagService.isEnabled("flag")) {
                System.out.println("Feature Flag is enabled");
            }  else {
                System.out.println("Feature Flag is disabled");
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}