package org.redflag.service;

import org.redflag.dto.CreateFeatureFlagRequest;
import org.redflag.dto.FeatureFlagsResponse;
import org.redflag.dto.SDKAuthResponse;
import org.redflag.dto.UsernamePasswordCredentials;

import java.util.HashMap;

public interface ServerRequestsService {
     SDKAuthResponse authenticateSDK(UsernamePasswordCredentials credentials);
     void createFeatureFlag(CreateFeatureFlagRequest createFeatureFlagRequest);
     void getNodeIdAndOrganizationId();
     HashMap<String, Boolean> getAllFeatureFlags();
}
