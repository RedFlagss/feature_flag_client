package org.redflag.service;

import org.redflag.dto.FeatureFlagTopicsRequest;
import org.redflag.dto.SDKAuthResponse;
import org.redflag.dto.UsernamePasswordCredentials;

public interface ServerRequestsService {

    public SDKAuthResponse authenticateSDK(UsernamePasswordCredentials credentials);

    public FeatureFlagTopicsRequest getFeatureFlagTopics();

    public void createFeatureFlag();

}
