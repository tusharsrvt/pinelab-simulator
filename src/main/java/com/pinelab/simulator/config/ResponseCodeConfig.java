package com.pinelab.simulator.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Loads response codes configuration from response-codes.yaml
 * Supports auto-reload when config file changes.
 */
@Slf4j
@Data
@Component
public class ResponseCodeConfig {
    
    private Map<String, String> responseCodes;
    
    @Value("${simulator.config.file:simulator-config.yaml}")
    private String configFilePath;
    
    @Value("${simulator.config.auto-reload:true}")
    private boolean autoReloadEnabled;
    
    private WatchService watchService;
    private ExecutorService watchExecutor;
    private volatile boolean running = true;
    
    private static final String RESPONSE_CODES_FILE = "response-codes.yaml";
    
    @PostConstruct
    public void loadConfig() {
        loadResponseCodes();
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
            } catch (Exception e) {
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
        log.info("ResponseCodeConfig shutdown complete");
    }
    
    /**
     * Load response codes from YAML file.
     * First checks filesystem (config/), then falls back to classpath.
     */
    private void loadResponseCodes() {
        Yaml yaml = new Yaml();
        try {
            // First try filesystem (allows external config modification)
            String[] searchPaths = {
                "config/response-codes.yaml",
                "target/dist/config/response-codes.yaml",
                "response-codes.yaml"
            };
            
            for (String path : searchPaths) {
                File configFile = new File(path);
                if (configFile.exists()) {
                    InputStream inputStream = new FileInputStream(configFile);
                    Map<String, Object> root = yaml.load(inputStream);
                    loadFromMap(root);
                    log.info("Response codes config loaded from filesystem: {}", path);
                    return;
                }
            }
            
            // Fallback to classpath
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(RESPONSE_CODES_FILE);
            if (inputStream != null) {
                Map<String, Object> root = yaml.load(inputStream);
                loadFromMap(root);
                log.info("Response codes config loaded from classpath");
                return;
            }
            
            log.warn("Response codes config file not found: {}", RESPONSE_CODES_FILE);
            responseCodes = Map.of("default", "Transaction failed.");
        } catch (Exception e) {
            log.error("Error loading response codes config", e);
            responseCodes = Map.of("default", "Transaction failed.");
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadFromMap(Map<String, Object> root) {
        if (root != null && root.get("response-codes") != null) {
            Map<String, Object> codesMap = (Map<String, Object>) root.get("response-codes");
            responseCodes = new java.util.HashMap<>();
            codesMap.forEach((key, value) -> responseCodes.put(key.toString(), value.toString()));
        } else {
            responseCodes = Map.of("default", "Transaction failed.");
        }
    }
    
    /**
     * Start watching the config file for changes.
     */
    private void startFileWatcher() {
        try {
            // Check both possible locations - config folder first, then current directory
            String[] searchPaths = {
                "config/response-codes.yaml",
                "target/dist/config/response-codes.yaml",
                "response-codes.yaml"
            };
            File configFile = null;
            
            for (String path : searchPaths) {
                File f = new File(path);
                if (f.exists()) {
                    configFile = f;
                    break;
                }
            }
            
            if (configFile == null) {
                log.info("Response codes file not on filesystem (likely in JAR), skipping file watcher");
                return;
            }
            
            Path configDir = configFile.getAbsoluteFile().getParentFile().toPath();
            
            watchService = FileSystems.getDefault().newWatchService();
            configDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            
            watchExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "response-codes-watcher"));
            watchExecutor.submit(this::watchForChanges);
            
            log.info("Auto-reload enabled. Watching for changes to: {}", configFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to start file watcher for response codes", e);
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
                        if (changedPath.toString().equals(RESPONSE_CODES_FILE)) {
                            log.info("Response codes file changed, reloading...");
                            // Add a small delay to ensure file write is complete
                            Thread.sleep(500);
                            loadResponseCodes();
                            log.info("Response codes reloaded successfully");
                        }
                    }
                }
                
                key.reset();
            } catch (InterruptedException e) {
                log.info("Response codes file watcher interrupted");
                break;
            } catch (Exception e) {
                log.error("Error watching response codes file", e);
            }
        }
    }
    
    /**
     * Get response message for a given response code.
     * Returns default message if code not found.
     */
    public String getResponseMessage(String responseCode) {
        if (responseCode == null || responseCode.isEmpty()) {
            return responseCodes != null ? 
                    responseCodes.getOrDefault("default", "Transaction failed.") : 
                    "Transaction failed.";
        }
        
        return responseCodes != null ? 
                responseCodes.getOrDefault(responseCode, 
                        responseCodes.getOrDefault("default", "Transaction failed.")) : 
                "Transaction failed.";
    }
}