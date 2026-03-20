package org.redflag.service;

public interface FeatureFlagService {
    boolean isEnabled(String flagKey);
}
