package com.pinelab.simulator.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Utility class to load and manage response templates from filesystem.
 * Looks for templates in: ./config/templates/ (filesystem) then classpath.
 */
@Slf4j
@Component
public class ResponseTemplateLoader {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> templates = new HashMap<>();
    
    // Map API names to template file names
    private static final Map<String, String> API_TEMPLATE_MAP;
    
    static {
        API_TEMPLATE_MAP = new HashMap<>();
        API_TEMPLATE_MAP.put("debit", "debit-response.json");
        API_TEMPLATE_MAP.put("debit-reversal", "debit-reversal-response.json");
        API_TEMPLATE_MAP.put("credit", "credit-response.json");
        API_TEMPLATE_MAP.put("credit-reversal", "credit-reversal-response.json");
        API_TEMPLATE_MAP.put("validate-address", "validate-address-response.json");
        API_TEMPLATE_MAP.put("mandate-block-fund", "mandate-block-fund-response.json");
        API_TEMPLATE_MAP.put("mandate-unblock-fund", "mandate-unblock-fund-response.json");
        API_TEMPLATE_MAP.put("check-status", "check-status-response.json");
        API_TEMPLATE_MAP.put("ncmc-credit", "ncmc-credit-response.json");
        API_TEMPLATE_MAP.put("ncmc-validate-address", "ncmc-validate-address-response.json");
        API_TEMPLATE_MAP.put("ncmc-credit-reversal", "ncmc-credit-reversal-response.json");
        API_TEMPLATE_MAP.put("ncmc-check-status", "ncmc-check-status-response.json");
    }
    
    @PostConstruct
    public void loadTemplates() {
        // Try filesystem first (templates/ or config/templates/)
        // Check multiple possible locations
        String[] searchPaths = {
            "templates",
            "config/templates",
            "target/dist/templates",
            "target/dist/config/templates"
        };
        
        for (String apiName : API_TEMPLATE_MAP.keySet()) {
            String templateFile = API_TEMPLATE_MAP.get(apiName);
            boolean loaded = false;
            
            for (String searchPath : searchPaths) {
                String fullPath = searchPath + "/" + templateFile;
                try {
                    File file = new File(fullPath);
                    if (file.exists()) {
                        String template = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                        templates.put(apiName, template);
                        log.info("Loaded template for API {} from filesystem: {}", apiName, fullPath);
                        loaded = true;
                        break;
                    }
                } catch (IOException e) {
                    log.debug("Failed to load template from {}: {}", fullPath, e.getMessage());
                }
            }
            
            if (!loaded) {
                log.warn("Template not found for API: {}", apiName);
            }
        }
        
        log.info("Loaded {} templates", templates.size());
    }
    
    /**
     * Get the template string for a given API name.
     * @param apiName the API name (e.g., "debit", "credit")
     * @return the template string, or null if not found
     */
    public String getTemplate(String apiName) {
        return templates.get(apiName);
    }
    
    /**
     * Replace placeholders in template with actual values.
     * @param apiName the API name
     * @param values map of placeholder names to values
     * @return the filled template, or null if template not found
     */
    public String fillTemplate(String apiName, Map<String, Object> values) {
        String template = templates.get(apiName);
        if (template == null) {
            log.warn("Template not found for API: {}", apiName);
            return null;
        }
        
        String result = template;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String placeholder = "$" + entry.getKey() + "$";
            Object value = entry.getValue();
            String replacement = formatValue(value);
            result = result.replace(placeholder, replacement);
        }
        
        return result;
    }
    
    /**
     * Format value for JSON - handles null, strings, numbers, etc.
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        // Return value as-is - template already has quotes around placeholders
        // so we don't add extra quotes
        return escapeJson(value.toString());
    }
    
    /**
     * Escape special characters for JSON string.
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    /**
     * Get parsed JSON template as JsonNode for more complex manipulation.
     */
    public JsonNode getTemplateAsJson(String apiName) {
        String template = templates.get(apiName);
        if (template == null) {
            return null;
        }
        try {
            return objectMapper.readTree(template);
        } catch (IOException e) {
            log.error("Failed to parse template for API: {}", apiName, e);
            return null;
        }
    }
}