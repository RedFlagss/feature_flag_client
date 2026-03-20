package org.redflag.service;

import org.redflag.dto.UsernamePasswordCredentials;

import java.util.Map;

public interface ServerRequestsService {

    public void registerSDK(UsernamePasswordCredentials credentials);

    public void createFeatureFlag();

}
