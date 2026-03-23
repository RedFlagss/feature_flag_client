package org.redflag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class FeatureFlagTopicsRequest {
    private final List<String> topics;
}
