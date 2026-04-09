package com.pinelab.simulator.service;

import com.pinelab.simulator.config.ConfigManager;
import com.pinelab.simulator.config.SimulatorConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service to handle idempotency checks.
 * Stores idempotency keys in JVM memory with configurable expiry.
 */
@Slf4j
@Service
public class IdempotencyService {

    private final ConcurrentHashMap<String, Long> idempotencyKeys = new ConcurrentHashMap<>();
    private final ConfigManager configManager;
    
    private ScheduledExecutorService cleanupExecutor;
    private boolean enabled = false;
    private int expiryHours = 3;

    public IdempotencyService(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @PostConstruct
    public void init() {
        loadConfig();
        
        if (enabled) {
            startCleanupScheduler();
            log.info("IdempotencyService enabled for APIs with {} hour expiry", expiryHours);
        } else {
            log.info("IdempotencyService is disabled");
        }
    }

    /**
     * Load configuration from ConfigManager.
     */
    public void loadConfig() {
        SimulatorConfig config = configManager.getConfig();
        if (config != null && config.getIdempotency() != null) {
            this.enabled = config.getIdempotency().isEnabled();
            this.expiryHours = config.getIdempotency().getExpiryHours();
            
            if (enabled) {
                log.info("Idempotency config - enabled: {}, expiryHours: {}, apis: {}", 
                    enabled, expiryHours, config.getIdempotency().getApis());
            }
        }
    }

    /**
     * Check if idempotency is enabled for a specific API.
     */
    public boolean isEnabledForApi(String apiName) {
        if (!enabled) {
            return false;
        }
        
        SimulatorConfig config = configManager.getConfig();
        if (config == null || config.getIdempotency() == null || 
            config.getIdempotency().getApis() == null) {
            return false;
        }
        
        return config.getIdempotency().getApis().contains(apiName);
    }

    /**
     * Check if idempotency key exists and is still valid for given API.
     * @param apiName the API name
     * @param idempotencyKey the key to check
     * @return true if duplicate within expiry period
     */
    public boolean isDuplicate(String apiName, String idempotencyKey) {
        if (!isEnabledForApi(apiName)) {
            return false;
        }
        
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            return false;
        }

        // Create composite key: apiName + idempotencyKey
        String compositeKey = apiName + "|" + idempotencyKey;
        
        Long expiryTime = idempotencyKeys.get(compositeKey);
        if (expiryTime == null) {
            return false;
        }

        if (System.currentTimeMillis() > expiryTime) {
            // Key expired, remove it
            idempotencyKeys.remove(compositeKey);
            return false;
        }

        log.info("Duplicate idempotency key detected for API: {}, key: {}", apiName, idempotencyKey);
        return true;
    }

    /**
     * Store idempotency key for given API.
     * @param apiName the API name
     * @param idempotencyKey the key to store
     */
    public void store(String apiName, String idempotencyKey) {
        if (!isEnabledForApi(apiName)) {
            return;
        }
        
        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
            String compositeKey = apiName + "|" + idempotencyKey;
            long expiryMs = expiryHours * 60 * 60 * 1000L;
            long expiryTime = System.currentTimeMillis() + expiryMs;
            idempotencyKeys.put(compositeKey, expiryTime);
            log.debug("Stored idempotency key for API: {}, key: {}, expires in: {} hours", 
                apiName, idempotencyKey, expiryHours);
        }
    }

    private void startCleanupScheduler() {
        cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "idempotency-cleanup");
            t.setDaemon(true);
            return t;
        });
        
        // Run cleanup every hour
        cleanupExecutor.scheduleAtFixedRate(
            this::cleanupExpiredKeys,
            60 * 60 * 1000L, // 1 hour
            60 * 60 * 1000L,
            TimeUnit.MILLISECONDS
        );
    }

    private void cleanupExpiredKeys() {
        long now = System.currentTimeMillis();
        int initialSize = idempotencyKeys.size();
        
        idempotencyKeys.entrySet().removeIf(entry -> entry.getValue() < now);
        
        int removed = initialSize - idempotencyKeys.size();
        if (removed > 0) {
            log.info("Cleaned up {} expired idempotency keys. Current size: {}", removed, idempotencyKeys.size());
        }
    }

    public int getActiveKeyCount() {
        cleanupExpiredKeys();
        return idempotencyKeys.size();
    }

    public void clearAll() {
        int size = idempotencyKeys.size();
        idempotencyKeys.clear();
        log.info("Cleared all {} idempotency keys", size);
    }
}