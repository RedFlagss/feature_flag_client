package org.redflag.service;

import org.redflag.dto.FeatureFlagDTO;
import org.redflag.dto.FeatureFlagUpdate;
import org.redflag.dto.FeatureFlagsResponse;

import java.util.HashMap;
import java.util.List;

public interface FeatureFlagService {
    boolean isEnabled(String flagKey);
}
