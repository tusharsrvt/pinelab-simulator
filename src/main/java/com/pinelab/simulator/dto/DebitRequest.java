package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Debit Transaction Request (Wallet Redeem)
 * Matches actual JPOS UPI request format
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebitRequest {
    
    @JsonProperty("BillAmount")
    private Double billAmount;
    
    @JsonProperty("IdempotencyKey")
    private String idempotencyKey;
    
    @JsonProperty("UPITxnType")
    private String upiTxnType;
    
    @JsonProperty("Amount")
    private Double amount;
    
    @JsonProperty("PayeeName")
    private String payeeName;
    
    @JsonProperty("MccCode")
    private String mccCode;
    
    @JsonProperty("InvoiceNumber")
    private String invoiceNumber;
    
    @JsonProperty("PayerName")
    private String payerName;
    
    @JsonProperty("PayerVPA")
    private String payerVPA;
    
    @JsonProperty("PayeeVPA")
    private String payeeVPA;
    
    @JsonProperty("MccType")
    private String mccType;
    
    @JsonProperty("Notes")
    private String notes;
}