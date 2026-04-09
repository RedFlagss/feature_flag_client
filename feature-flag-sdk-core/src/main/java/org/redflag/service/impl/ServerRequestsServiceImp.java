package org.redflag.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redflag.data.Data;
import org.redflag.dto.*;
import org.redflag.service.ServerRequestsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class ServerRequestsServiceImp implements ServerRequestsService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static String accessToken;
    private static String nodeId;
    private static String organizationId;
    private static String uuId;

    public ServerRequestsServiceImp(String uuId) {
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
        this.uuId = uuId;
    }

    @Override
    public SDKAuthResponse authenticateSDK(UsernamePasswordCredentials credentials) {
        try {
            String jsonRequest = new ObjectMapper().writeValueAsString(credentials);
            System.out.println(jsonRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Data.AUTH_SERVICE_URL + Data.SDK_LOGIN_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if  (response.statusCode() == 200) {
                System.out.println(response.body());
                SDKAuthResponse sdkAuthResponse = objectMapper.readValue(response.body(), SDKAuthResponse.class);
                ServerRequestsServiceImp.accessToken = sdkAuthResponse.getAccess_token();
                return  objectMapper.readValue(response.body(), SDKAuthResponse.class);
            } else {
                throw new RuntimeException("Failed to authenticate SDK: HTTP error code : " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void createFeatureFlag(CreateFeatureFlagRequest createFeatureFlagRequest) {
        try {
            String urlString = String.format(
                    Data.FF_SERVICE_URL + Data.CREATE_FEATURE_FLAG_URL,
                    ServerRequestsServiceImp.organizationId, ServerRequestsServiceImp.nodeId
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(createFeatureFlagRequest);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getNodeIdAndOrganizationId() {

        try {
            String urlString = String.format(
                    Data.FF_SERVICE_URL + Data.FIND_NODE_URL,
                    ServerRequestsServiceImp.uuId);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();

            ObjectMapper objectMapper = new ObjectMapper();
            NodeResponse nodeResponse = objectMapper.readValue(jsonResponse, NodeResponse.class);

            ServerRequestsServiceImp.nodeId = nodeResponse.getId();
            ServerRequestsServiceImp.organizationId = nodeResponse.getOrganizationId();

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<String, Boolean> getAllFeatureFlags() {
        try {
            String urlString = String.format(
                    Data.FF_SERVICE_URL + Data.GET_ALL_FEATURE_FLAGS_URL,
                    ServerRequestsServiceImp.uuId
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/json")
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                FeatureFlagsResponse flagsResponse = objectMapper.readValue(
                        response.body(),
                        FeatureFlagsResponse.class
                );

                List<FeatureFlagDTO> featureFlagDTOS = flagsResponse.getItems();
                HashMap<String, Boolean> flagMap = new HashMap<>();
                for (FeatureFlagDTO item : featureFlagDTOS) {
                    flagMap.put(item.getName(), item.isValue());
                }

                return flagMap;
            } else {
                System.err.println("Failed to find flags. Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}
