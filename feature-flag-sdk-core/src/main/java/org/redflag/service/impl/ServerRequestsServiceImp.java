package org.redflag.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redflag.dto.FeatureFlagTopicsRequest;
import org.redflag.dto.SDKAuthResponse;
import org.redflag.dto.UsernamePasswordCredentials;
import org.redflag.service.ServerRequestsService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerRequestsServiceImp implements ServerRequestsService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ServerRequestsServiceImp() {
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public SDKAuthResponse authenticateSDK(UsernamePasswordCredentials credentials) {
        try {
            String jsonRequest = new ObjectMapper().writeValueAsString(credentials);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/sdk/login")) //Наверно надо подтягивать из конфига или организовать properties
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());



            if  (response.statusCode() == 200) {
                return  objectMapper.readValue(response.body(), SDKAuthResponse.class);
            } else {
                throw new RuntimeException("Failed to authenticate SDK: HTTP error code : " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FeatureFlagTopicsRequest getFeatureFlagTopics() {
        //TODO: получение топиков с feature-flag-service
        return null;
    }

    @Override
    public void createFeatureFlag() {

    }
}
