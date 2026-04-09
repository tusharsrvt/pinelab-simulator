package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Base class for all CBS transaction requests.
 * Contains common fields that appear in all transaction types.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseRequest {
    
    @JsonProperty("MsgId")
    private String msgId;
    
    @JsonProperty("TxnId")
    private String txnId;
    
    @JsonProperty("TxnDate")
    private String txnDate;
    
    @JsonProperty("OrgTxnId")
    private String orgTxnId;
    
    @JsonProperty("OrgTxnDate")
    private String orgTxnDate;
    
    @JsonProperty("DeviceId")
    private String deviceId;
    
    @JsonProperty("ClientId")
    private String clientId;
    
    @JsonProperty("TokenId")
    private String tokenId;
    
    @JsonProperty("AccRefNo")
    private String accRefNo;
    
    @JsonProperty("BankCode")
    private String bankCode;
    
    @JsonProperty("ChannelId")
    private String channelId;
    
    @JsonProperty("BranchId")
    private String branchId;
    
    @JsonProperty("InstitutionId")
    private String institutionId;
    
    @JsonProperty("SettlementCycleId")
    private String settlementCycleId;
    
    @JsonProperty("UtrNo")
    private String utrNo;
}