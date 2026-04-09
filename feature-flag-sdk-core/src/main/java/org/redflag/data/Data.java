package org.redflag.data;

import org.apache.kafka.common.protocol.types.Field;

public class Data {
    public static String FF_SERVICE_URL = "http://localhost:8080";
    public static String AUTH_SERVICE_URL = "http://localhost:8081";

    public static String SDK_LOGIN_URL = "/api/v1/sdk/login";
    public static String CREATE_FEATURE_FLAG_URL = "/api/v1/organizations/%s/nodes/%s/feature-flags";
    public static String FIND_NODE_URL = "/api/v1/find-node?organizationNodeUuid=%s";
    public static String GET_ALL_FEATURE_FLAGS_URL = "/api/v1/find-flags?organizationNodeUuid=%s";
}
