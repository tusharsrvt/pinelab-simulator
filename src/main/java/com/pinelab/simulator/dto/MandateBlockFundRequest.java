package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Mandate Block Fund Request
 * POST /api/{accountNumber}/mandate-block-fund
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MandateBlockFundRequest {
    
    @JsonProperty("BillAmount")
    private String billAmount;
    
    @JsonProperty("IdempotencyKey")
    private String idempotencyKey;
    
    @JsonProperty("UPITxnType")
    private String upiTxnType;
    
    @JsonProperty("Amount")
    private String amount;
    
    @JsonProperty("PayeeName")
    private String payeeName;
    
    @JsonProperty("MccCode")
    private String mccCode;
    
    @JsonProperty("Mandate")
    private Mandate mandate;
    
    @JsonProperty("InvoiceNumber")
    private String invoiceNumber;
    
    @JsonProperty("PayerVPA")
    private String payerVPA;
    
    @JsonProperty("PayerName")
    private String payerName;
    
    @JsonProperty("PayeeVPA")
    private String payeeVPA;
    
    @JsonProperty("MccType")
    private String mccType;
    
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