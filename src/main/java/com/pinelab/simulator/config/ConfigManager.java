package com.pinelab.simulator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages the simulator configuration, including loading from JSON file
 * and providing runtime updates. Supports auto-reload when config file changes.
 */
@Slf4j
@Component
public class ConfigManager {
    
    private final ObjectMapper jsonMapper;
    
    @Value("${simulator.config.file:simulator-config.json}")
    private String configFilePath;
    
    @Value("${simulator.config.auto-reload:true}")
    private boolean autoReloadEnabled;
    
    private SimulatorConfig config;
    private WatchService watchService;
    private ExecutorService watchExecutor;
    private volatile boolean running = true;
    
    public ConfigManager() {
        this.jsonMapper = new ObjectMapper();
    }
    
    @PostConstruct
    public void init() {
        loadConfig();
        if (autoReloadEnabled) {
            startFileWatcher();
        }
    }
    
    @PreDestroy
    public void cleanup() {
        running = false;
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.error("Error closing watch service", e);
            }
        }
        if (watchExecutor != null) {
            watchExecutor.shutdown();
            try {
                if (!watchExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    watchExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                watchExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        log.info("ConfigManager shutdown complete");
    }
    
    /**
     * Start watching the config file for changes.
     */
    private void startFileWatcher() {
        try {
            // Check both possible locations - config folder first, then current directory
            String[] searchPaths = {"config/simulator-config.json", "simulator-config.json"};
            File configFile = null;
            
            for (String path : searchPaths) {
                File f = new File(path);
                if (f.exists()) {
                    configFile = f;
                    break;
                }
            }
            
            if (configFile == null) {
                log.info("Config file not on filesystem (likely in JAR), skipping file watcher");
                return;
            }
            
            // Update the configFilePath to match what was found
            this.configFilePath = configFile.getPath();
            
            Path configDir = configFile.getAbsoluteFile().getParentFile().toPath();
            
            watchService = FileSystems.getDefault().newWatchService();
            configDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            
            watchExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "config-file-watcher"));
            watchExecutor.submit(this::watchForChanges);
            
            log.info("Auto-reload enabled. Watching for changes to: {}", configFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to start file watcher", e);
        }
    }
    
    /**
     * Watch loop that monitors config file changes.
     */
    private void watchForChanges() {
        while (running) {
            try {
                WatchKey key = watchService.take();
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        Path changedPath = (Path) event.context();
                        if (changedPath.toString().equals(new File(configFilePath).getName())) {
                            log.info("Config file changed, reloading...");
                            // Add a small delay to ensure file write is complete
                            Thread.sleep(500);
                            loadConfig();
                            log.info("Config reloaded successfully");
                        }
                    }
                }
                
                key.reset();
            } catch (InterruptedException e) {
                log.info("File watcher interrupted");
                break;
            } catch (Exception e) {
                log.error("Error watching config file", e);
            }
        }
    }
    
    /**
     * Load configuration from the JSON file.
     * First checks filesystem (./config/), then falls back to classpath.
     */
    public synchronized void loadConfig() {
        try {
            // First try file system (allows external config modification)
            // Check multiple possible locations (dist/, config/, and root)
            String[] searchPaths = {
                "config/simulator-config.json",
                "target/dist/config/simulator-config.json",
                "target/classes/config/simulator-config.json",
                "simulator-config.json"
            };
            
            for (String path : searchPaths) {
                File configFile = new File(path);
                if (configFile.exists()) {
                    SimulatorConfig loadedConfig = jsonMapper.readValue(configFile, SimulatorConfig.class);
                    this.config = loadedConfig;
                    log.info("Loaded simulator configuration (JSON) from filesystem: {}", path);
                    return;
                }
            }
            
            // Fall back to classpath (src/main/resources)
            String[] classpathPaths = {
                "config/simulator-config.json"
            };
            
            for (String path : classpathPaths) {
                try {
                    var resource = new org.springframework.core.io.ClassPathResource(path);
                    if (resource.exists()) {
                        SimulatorConfig loadedConfig = jsonMapper.readValue(resource.getInputStream(), SimulatorConfig.class);
                        this.config = loadedConfig;
                        log.info("Loaded simulator configuration from classpath: {}", path);
                        return;
                    }
                } catch (Exception e) {
                    // Continue to next path
                }
            }
            
            log.warn("Config file not found, using defaults");
            this.config = getDefaultConfig();
        } catch (IOException e) {
            log.error("Error loading config file, using defaults", e);
            this.config = getDefaultConfig();
        }
    }
    
    /**
     * Get the current configuration.
     */
    public SimulatorConfig getConfig() {
        return config;
    }
    
    /**
     * Get configuration for a specific API.
     */
    public SimulatorConfig.ApiConfig getApiConfig(String apiName) {
        if (config != null && config.getApis() != null && config.getApis().containsKey(apiName)) {
            return config.getApis().get(apiName);
        }
        return config != null ? config.getDefaultConfig() : getDefaultApiConfig();
    }
    
    /**
     * Get configuration for a specific API, matching against account number rules.
     * 
     * @param apiName The API name (e.g., "debit", "credit")
     * @param accountNumber The full account number to match against rules
     * @return The matching ApiConfig (with rules applied), or default config if no match
     */
    public SimulatorConfig.ApiConfig getApiConfig(String apiName, String accountNumber) {
        SimulatorConfig.ApiConfig apiConfig = getApiConfig(apiName);
        
        if (apiConfig == null) {
            return getDefaultApiConfig();
        }
        
        // If no rules defined, return the API config as-is (backward compatibility)
        if (apiConfig.getRules() == null || apiConfig.getRules().isEmpty()) {
            return apiConfig;
        }
        
        // Extract last 4 digits
        String last4Digits = extractLast4Digits(accountNumber);
        
        // Match against rules
        SimulatorConfig.Rule matchedRule = com.pinelab.simulator.util.RuleMatcher.match(
                last4Digits, apiConfig.getRules());
        
        if (matchedRule != null) {
            // Create a new ApiConfig with matched rule's values
            SimulatorConfig.ApiConfig matchedConfig = new SimulatorConfig.ApiConfig();
            matchedConfig.setResponseCode(matchedRule.getResponseCode());
            matchedConfig.setResponseTimeMs(matchedRule.getResponseTimeMs());
            matchedConfig.setTimeout(matchedRule.isTimeout());
            matchedConfig.setRules(apiConfig.getRules()); // Keep rules for reference
            log.debug("Account {} matched rule for API {}", last4Digits, apiName);
            return matchedConfig;
        }
        
        // No rule matched, use the API config as-is
        return apiConfig;
    }
    
    /**
     * Extract last 4 digits from account number.
     */
    private String extractLast4Digits(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return "";
        }
        int length = accountNumber.length();
        return length >= 4 ? accountNumber.substring(length - 4) : accountNumber;
    }
    
    /**
     * Update configuration for a specific API at runtime.
     */
    public void updateApiConfig(String apiName, String matchPattern, String responseCode, Long responseTimeMs, Boolean timeout) {
        if (config == null) {
            config = new SimulatorConfig();
        }
        if (config.getApis() == null) {
            config.setApis(new HashMap<>());
        }
        
        SimulatorConfig.ApiConfig apiConfig = config.getApis().getOrDefault(apiName, new SimulatorConfig.ApiConfig());
        
        if (matchPattern != null && !matchPattern.isEmpty()) {
            // Add a new rule with the specified match pattern
            SimulatorConfig.Rule newRule = new SimulatorConfig.Rule();
            newRule.setMatch(java.util.List.of(matchPattern));
            newRule.setResponseCode(responseCode != null ? responseCode : "0000");
            newRule.setResponseTimeMs(responseTimeMs != null ? responseTimeMs : 0L);
            newRule.setTimeout(timeout != null ? timeout : false);
            apiConfig.getRules().add(newRule);
        } else if (responseCode != null) {
            apiConfig.setResponseCode(responseCode);
        }
        if (responseTimeMs != null) {
            apiConfig.setResponseTimeMs(responseTimeMs);
        }
        if (timeout != null) {
            apiConfig.setTimeout(timeout);
        }
        
        config.getApis().put(apiName, apiConfig);
        log.info("Updated config for API '{}': matchPattern={}, responseCode={}, responseTimeMs={}, timeout={}",
                apiName, matchPattern, responseCode, responseTimeMs, timeout);
    }
    
    /**
     * Save current configuration to file.
     */
    public void saveConfig() {
        try {
            // Determine file extension based on configFilePath
            String filePath = configFilePath;
            if (!filePath.endsWith(".json")) {
                // Default to .json if not specified
                filePath = filePath.replaceAll("\\.[^.]+$", "") + ".json";
            }
            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), config);
            log.info("Saved configuration to {}", filePath);
        } catch (IOException e) {
            log.error("Error saving config file", e);
        }
    }
    
    /**
     * Check if auto-reload is enabled.
     */
    public boolean isAutoReloadEnabled() {
        return autoReloadEnabled;
    }
    
    private SimulatorConfig getDefaultConfig() {
        SimulatorConfig defaultConfig = new SimulatorConfig();
        defaultConfig.setDefaultConfig(getDefaultApiConfig());
        
        Map<String, SimulatorConfig.ApiConfig> defaultApis = new HashMap<>();
        String[] apiNames = {"debit", "debit-reversal", "credit", "credit-reversal", 
                            "validate-address", "mandate-block-fund", "mandate-unblock-fund", "check-status"};
        for (String apiName : apiNames) {
            defaultApis.put(apiName, getDefaultApiConfig());
        }
        defaultConfig.setApis(defaultApis);
        
        return defaultConfig;
    }
    
    private SimulatorConfig.ApiConfig getDefaultApiConfig() {
        SimulatorConfig.ApiConfig apiConfig = new SimulatorConfig.ApiConfig();
        apiConfig.setResponseCode("0000");
        apiConfig.setResponseTimeMs(0L);
        apiConfig.setTimeout(false);
        return apiConfig;
    }
}