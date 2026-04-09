package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response DTO for all CBS transaction APIs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbsResponse {
    
    @JsonProperty("MsgId")
    private String msgId;
    
    @JsonProperty("TxnId")
    private String txnId;
    
    @JsonProperty("RespCode")
    private String respCode;
    
    @JsonProperty("RespMsg")
    private String respMsg;
    
    @JsonProperty("BankCode")
    private String bankCode;
    
    @JsonProperty("UtrNo")
    private String utrNo;
    
    @JsonProperty("TxnDate")
    private String txnDate;
    
    @JsonProperty("Amount")
    private String amount;
    
    @JsonProperty("Status")
    private String status;
    
    @JsonProperty("AddlInfo1")
    private String addlInfo1;
    
    @JsonProperty("AddlInfo2")
    private String addlInfo2;
    
    @JsonProperty("AddlInfo3")
    private String addlInfo3;
    
    @JsonProperty("AddlInfo4")
    private String addlInfo4;
    
    @JsonProperty("AddlInfo5")
    private String addlInfo5;
}