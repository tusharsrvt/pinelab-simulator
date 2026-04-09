package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * NCMC Credit Reversal Request
 * POST /api/{accountNumber}/ncmc-credit-reversal
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NcmcCreditReversalRequest {
    
    @JsonProperty("transactionMode")
    private Integer transactionMode;
    
    @JsonProperty("invoiceNumber")
    private String invoiceNumber;
    
    @JsonProperty("inputType")
    private String inputType;
    
    @JsonProperty("walletNumber")
    private String walletNumber;
    
    @JsonProperty("accounts")
    private List<Account> accounts;
    
    @JsonProperty("originalBatchNumber")
    private Long originalBatchNumber;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Account {
        @JsonProperty("number")
        private String number;
        
        @JsonProperty("amount")
        private Double amount;
        
        @JsonProperty("currencyCode")
        private String currencyCode;
    }
}