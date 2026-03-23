package org.redflag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class SDKAuthResponse {
    private final String access_token;
    private final String token_type;
}
