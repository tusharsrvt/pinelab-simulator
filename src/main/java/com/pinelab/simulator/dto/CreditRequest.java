package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Credit Transaction Request (Add Card to Wallet / Credit)
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditRequest {
    
    @JsonProperty("IdempotencyKey")
    private String idempotencyKey;
    
    @JsonProperty("Amount")
    private Double amount;
    
    @JsonProperty("PayeeName")
    private String payeeName;
    
    @JsonProperty("CardProgramName")
    private String cardProgramName;
    
    @JsonProperty("PayerName")
    private String payerName;
    
    @JsonProperty("PayerVPA")
    private String payerVPA;
    
    @JsonProperty("PayeeVPA")
    private String payeeVPA;
    
    @JsonProperty("MccType")
    private String mccType;
    
    @JsonProperty("UPITxnType")
    private String upiTxnType;
    
    @JsonProperty("MccCode")
    private String mccCode;
    
    @JsonProperty("InvoiceNumber")
    private String invoiceNumber;
    
    @JsonProperty("CreditType")
    private String creditType;
    
    @JsonProperty("Notes")
    private String notes;
}