package org.redflag.service.impl;

import org.redflag.configuration.AppConfig;
import org.redflag.server.DataStorage;
import org.redflag.service.FeatureFlagService;

public class FeatureFlagServiceImp implements FeatureFlagService {
    DataStorage dataStorage;

    public FeatureFlagServiceImp(AppConfig appConfig) {
        dataStorage = new DataStorage();
    }

    @Override
    public boolean isEnabled(String flagKey) {
        return dataStorage.getValue(flagKey);
    }
}
