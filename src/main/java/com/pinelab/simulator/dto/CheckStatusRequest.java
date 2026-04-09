package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Check Status Request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckStatusRequest extends BaseRequest {
    
    @JsonProperty("TxnRefNo")
    private String txnRefNo;
    
    @JsonProperty("QueryType")
    private String queryType;
}