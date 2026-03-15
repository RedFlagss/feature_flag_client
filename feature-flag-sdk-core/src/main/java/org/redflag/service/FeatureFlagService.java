package org.redflag.service;

import org.redflag.configuration.AppConfig;
import org.redflag.service.impl.FeatureFlagServiceImp;

public interface FeatureFlagService {
    boolean isEnabled(String flagKey);

    static Builder builder() {
        return new Builder();
    }

    class Builder {
        private String backendUrl;
        private String sdkLogin;
        private String sdkPassword;

        public Builder backendUrl(String backendUrl) {
            this.backendUrl = backendUrl;
            return this;
        }

        public Builder sdkLogin(String sdkLogin) {
            this.sdkLogin = sdkLogin;
            return this;
        }

        public Builder sdkPassword(String sdkPassword) {
            this.sdkPassword = sdkPassword;
            return this;
        }

        public FeatureFlagService build() {
            AppConfig appConfig = new AppConfig(backendUrl, sdkLogin, sdkPassword);

            return new FeatureFlagServiceImp(appConfig);
        }
    }
}
