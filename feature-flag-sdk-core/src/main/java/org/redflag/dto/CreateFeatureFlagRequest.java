package org.redflag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateFeatureFlagRequest {
    private final String name;
    private final Boolean value;
}
