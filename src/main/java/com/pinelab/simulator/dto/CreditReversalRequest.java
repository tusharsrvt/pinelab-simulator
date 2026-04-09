package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Credit Reversal Request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditReversalRequest extends BaseRequest {
    
    @JsonProperty("Amount")
    private String amount;
    
    @JsonProperty("BeneficiaryAccNo")
    private String beneficiaryAccNo;
    
    @JsonProperty("ReversalReason")
    private String reversalReason;
    
    @JsonProperty("OriginalRRN")
    private String originalRRN;
    
    @JsonProperty("AddlInfo1")
    private String addlInfo1;
    
    @JsonProperty("AddlInfo2")
    private String addlInfo2;
}