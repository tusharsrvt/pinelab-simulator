package com.pinelab.simulator.controller;

import com.pinelab.simulator.config.ConfigManager;
import com.pinelab.simulator.config.SimulatorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for managing simulator configuration at runtime.
 * Allows QA to update API behavior without restarting the application.
 */
@Slf4j
@RestController
@RequestMapping("/simulator")
public class SimulatorConfigController {
    
    private final ConfigManager configManager;
    
    public SimulatorConfigController(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * Get current configuration for all APIs.
     * GET /simulator/config
     */
    @GetMapping("/config")
    public ResponseEntity<SimulatorConfig> getConfig() {
        return ResponseEntity.ok(configManager.getConfig());
    }
    
    /**
     * Get configuration for a specific API.
     * GET /simulator/config/{apiName}
     */
    @GetMapping("/config/{apiName}")
    public ResponseEntity<SimulatorConfig.ApiConfig> getApiConfig(@PathVariable String apiName) {
        SimulatorConfig.ApiConfig config = configManager.getApiConfig(apiName);
        return ResponseEntity.ok(config);
    }
    
    /**
     * Update configuration for a specific API.
     * PUT /simulator/config/{apiName}
     * 
     * Request body example:
     * {
     *   "matchPattern": "1234",
     *   "responseCode": "10001",
     *   "responseTimeMs": 5000,
     *   "timeout": false
     * }
     */
    @PutMapping("/config/{apiName}")
    public ResponseEntity<Map<String, String>> updateApiConfig(
            @PathVariable String apiName,
            @RequestBody ApiConfigRequest request) {
        
        log.info("Updating config for API '{}': {}", apiName, request);
        
        configManager.updateApiConfig(
                apiName,
                request.getMatchPattern(),
                request.getResponseCode(),
                request.getResponseTimeMs(),
                request.getTimeout()
        );
        
        // Optionally save to file
        configManager.saveConfig();
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Configuration updated for API: " + apiName
        ));
    }
    
    /**
     * Reset configuration for a specific API to defaults.
     * DELETE /simulator/config/{apiName}
     */
    @DeleteMapping("/config/{apiName}")
    public ResponseEntity<Map<String, String>> resetApiConfig(@PathVariable String apiName) {
        log.info("Resetting config for API '{}'", apiName);
        
        configManager.updateApiConfig(apiName, null, "0000", 0L, false);
        configManager.saveConfig();
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Configuration reset to defaults for API: " + apiName
        ));
    }
    
    /**
     * Reload configuration from file.
     * POST /simulator/config/reload
     */
    @PostMapping("/config/reload")
    public ResponseEntity<Map<String, String>> reloadConfig() {
        log.info("Reloading configuration from file");
        configManager.loadConfig();
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Configuration reloaded from file"
        ));
    }
    
    /**
     * Request body DTO for updating API configuration.
     */
    public static class ApiConfigRequest {
        private String matchPattern;
        private String responseCode;
        private Long responseTimeMs;
        private Boolean timeout;
        
        public String getMatchPattern() {
            return matchPattern;
        }
        
        public void setMatchPattern(String matchPattern) {
            this.matchPattern = matchPattern;
        }
        
        public String getResponseCode() {
            return responseCode;
        }
        
        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }
        
        public Long getResponseTimeMs() {
            return responseTimeMs;
        }
        
        public void setResponseTimeMs(Long responseTimeMs) {
            this.responseTimeMs = responseTimeMs;
        }
        
        public Boolean getTimeout() {
            return timeout;
        }
        
        public void setTimeout(Boolean timeout) {
            this.timeout = timeout;
        }
    }
}