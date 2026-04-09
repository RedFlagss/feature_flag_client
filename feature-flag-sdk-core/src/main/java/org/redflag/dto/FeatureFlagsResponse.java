package org.redflag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagsResponse {
    private String nodeUuid;
    private List<FeatureFlagDTO> items;
    private Integer total;
}
