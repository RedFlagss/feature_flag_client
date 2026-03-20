package org.redflag.configuration;


public class AppConfig {
    private String backendUrl;
    private String sdkLogin;
    private String sdkPassword;

    public AppConfig(String backendUrl, String sdkLogin, String sdkPassword) {
        this.backendUrl = backendUrl;
        this.sdkLogin = sdkLogin;
        this.sdkPassword = sdkPassword;
    }
}
