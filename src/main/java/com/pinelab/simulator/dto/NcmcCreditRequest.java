package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * NCMC Credit Request
 * POST /api/{accountNumber}/ncmc-credit
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NcmcCreditRequest {
    
    @JsonProperty("notes")
    private String notes;
    
    @JsonProperty("idempotencyKey")
    private String idempotencyKey;
    
    @JsonProperty("invoiceNumber")
    private String invoiceNumber;
    
    @JsonProperty("invoiceAmount")
    private Double invoiceAmount;
    
    @JsonProperty("walletNumber")
    private String walletNumber;
    
    @JsonProperty("accounts")
    private List<Account> accounts;
    
    @JsonProperty("transactionModeId")
    private String transactionModeId;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Account {
        @JsonProperty("number")
        private String number;
        
        @JsonProperty("payeeName")
        private String payeeName;
        
        @JsonProperty("loadType")
        private String loadType;
        
        @JsonProperty("amount")
        private String amount;
        
        @JsonProperty("payeeVpa")
        private String payeeVpa;
        
        @JsonProperty("payerName")
        private String payerName;
        
        @JsonProperty("toPocket")
        private String toPocket;
        
        @JsonProperty("payerVpa")
        private String payerVpa;
        
        @JsonProperty("currencyCode")
        private String currencyCode;
    }
}