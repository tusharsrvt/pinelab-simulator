package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * NCMC Validate Address Request
 * POST /api/{accountNumber}/ncmc-validate-address
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NcmcValidateAddressRequest {
    
    @JsonProperty("vpa")
    private String vpa;
}