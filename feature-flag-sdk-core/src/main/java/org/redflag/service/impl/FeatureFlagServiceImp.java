package org.redflag.service.impl;

import org.redflag.server.DataStorage;
import org.redflag.service.FeatureFlagService;

public class FeatureFlagServiceImp implements FeatureFlagService {
    DataStorage dataStorage;

    public FeatureFlagServiceImp(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public boolean isEnabled(String flagKey) {
        return dataStorage.getValue(flagKey);
    }
}
