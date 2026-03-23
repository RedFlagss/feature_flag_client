package org.redflag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FeatureFlagUpdate {
    private String flagKey;
    private Boolean flagValue;
}
