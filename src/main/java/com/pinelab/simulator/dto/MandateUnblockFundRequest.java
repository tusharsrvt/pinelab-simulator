package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Mandate Unblock Fund Request
 * POST /api/{accountNumber}/mandate-unblock-fund
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MandateUnblockFundRequest {
    
    @JsonProperty("OriginalBatchNumber")
    private Long originalBatchNumber;
    
    @JsonProperty("OriginalTransactionId")
    private String originalTransactionId;
    
    @JsonProperty("PreAuthCode")
    private String preAuthCode;
    
    @JsonProperty("Mandate")
    private Mandate mandate;
    
    @JsonProperty("ApprovalCode")
    private String approvalCode;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Mandate {
        @JsonProperty("Type")
        private String type;
        
        @JsonProperty("UMN")
        private String umn;
        
        @JsonProperty("RecurrencePattern")
        private String recurrencePattern;
        
        @JsonProperty("Purpose")
        private String purpose;
        
        @JsonProperty("RefCategory")
        private String refCategory;
    }
}