package org.redflag;

import org.redflag.server.DataStorage;
import org.redflag.service.FeatureFlagService;
import org.redflag.service.impl.FeatureFlagServiceImp;

public class Main {
    public static void main(String[] args) {
        DataStorage dataStorage = new DataStorage();
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