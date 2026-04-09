package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Debit Reversal Request (Wallet Cancel Redeem)
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebitReversalRequest {
    
    @JsonProperty("OriginalBatchNumber")
    private String originalBatchNumber;
    
    @JsonProperty("OriginalTransactionId")
    private String originalTransactionId;
    
    @JsonProperty("PayeeName")
    private String payeeName;
    
    @JsonProperty("MccCode")
    private String mccCode;
    
    @JsonProperty("InvoiceNumber")
    private String invoiceNumber;
    
    @JsonProperty("PayerName")
    private String payerName;
    
    @JsonProperty("MccType")
    private String mccType;
    
    @JsonProperty("Notes")
    private String notes;
    
    @JsonProperty("ApprovalCode")
    private String approvalCode;
}