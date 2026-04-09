package org.redflag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeResponse {
    private String id;
    private String organizationId;
    private String uuid;
    private String path;
    private String name;
    private String isService;
    private String version;
}