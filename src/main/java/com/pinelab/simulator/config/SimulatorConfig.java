package com.pinelab.simulator.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for each API's behavior.
 * Each API can have a specific response code and delay configured.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cbs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimulatorConfig {
    
    /**
     * Map of API name to its configuration.
     */
    private Map<String, ApiConfig> apis = new HashMap<>();
    
    /**
     * Default configuration used when no specific config matches.
     */
    private ApiConfig defaultConfig = new ApiConfig();
    
    /**
     * Idempotency configuration.
     */
    private IdempotencyConfig idempotency = new IdempotencyConfig();
    
    /**
     * API Configuration with rule-based matching.
     */
    @Data
    public static class ApiConfig {
        private String responseCode = "0000";
        private Long responseTimeMs = 0L;
        private boolean timeout = false;
        
        /**
         * List of rules for matching account numbers.
         * First matching rule wins.
         */
        private List<Rule> rules = new ArrayList<>();
    }
    
    /**
     * Rule for matching specific account numbers or ranges.
     */
    @Data
    public static class Rule {
        /**
         * List of matching patterns. Can be:
         * - Exact value: "1234"
         * - Wildcard pattern: "00.*" (regex)
         * - Range: ["0000", "9999"]
         */
        private List<String> match = new ArrayList<>();
        
        /**
         * Response code for matched requests.
         */
        private String responseCode = "0000";
        
        /**
         * Response time in milliseconds.
         */
        private Long responseTimeMs = 0L;
        
        /**
         * Whether to simulate timeout.
         */
        private boolean timeout = false;
    }
    
    /**
     * Idempotency feature configuration.
     */
    @Data
    public static class IdempotencyConfig {
        /**
         * Enable/disable idempotency check.
         */
        private boolean enabled = false;
        
        /**
         * Expiry time in hours for idempotency keys.
         */
        private int expiryHours = 3;
        
        /**
         * List of API names where idempotency check is applied.
         */
        private List<String> apis = new ArrayList<>();
    }
}