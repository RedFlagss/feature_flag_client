package org.redflag.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redflag.dto.CreateFeatureFlagRequest;
import org.redflag.dto.UsernamePasswordCredentials;
import org.redflag.service.ServerRequestsService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerRequestsServiceImp implements ServerRequestsService {

    @Override
    public void registerSDK(UsernamePasswordCredentials credentials) {
        HttpClient httpClient = HttpClient.newHttpClient();

        String jsonRequest;
        try {
            jsonRequest = new ObjectMapper().writeValueAsString(credentials);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(""))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createFeatureFlag() {

    }
}
