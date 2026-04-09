package com.pinelab.simulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Debit Transaction Response (Wallet Redeem Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitResponse {
    
    @JsonProperty("Cards")
    private List<Card> cards;
    
    @JsonProperty("CurrentBatchNumber")
    private Long currentBatchNumber;
    
    @JsonProperty("WalletNumber")
    private String walletNumber;
    
    @JsonProperty("InvoiceNumber")
    private String invoiceNumber;
    
    @JsonProperty("DateAtServer")
    private String dateAtServer;
    
    @JsonProperty("BatchNumber")
    private Long batchNumber;
    
    @JsonProperty("Amount")
    private Double amount;
    
    @JsonProperty("Balance")
    private Double balance;
    
    @JsonProperty("ConsolidatedBalance")
    private Double consolidatedBalance;
    
    @JsonProperty("BillAmount")
    private Double billAmount;
    
    @JsonProperty("WalletPIN")
    private String walletPIN;
    
    @JsonProperty("ExcludedBucketsBalance")
    private Double excludedBucketsBalance;
    
    @JsonProperty("UPIXactionType")
    private String upiXactionType;
    
    @JsonProperty("Notes")
    private String notes;
    
    @JsonProperty("ApprovalCode")
    private String approvalCode;
    
    @JsonProperty("ResponseCode")
    private Integer responseCode;
    
    @JsonProperty("ResponseMessage")
    private String responseMessage;
    
    @JsonProperty("TransactionId")
    private Long transactionId;
    
    @JsonProperty("TransactionType")
    private String transactionType;
    
    @JsonProperty("ErrorCode")
    private String errorCode;
    
    @JsonProperty("ErrorDescription")
    private String errorDescription;
    
    @JsonProperty("PreAuthCode")
    private String preAuthCode;
    
    @JsonProperty("GeoLocation")
    private String geoLocation;
    
    /**
     * Card details sub-object
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        
        @JsonProperty("RedeemStartDate")
        private String redeemStartDate;
        
        @JsonProperty("CardNumber")
        private String cardNumber;
        
        @JsonProperty("Amount")
        private Double amount;
        
        @JsonProperty("CardBalance")
        private Double cardBalance;
        
        @JsonProperty("CardProgramName")
        private String cardProgramName;
        
        @JsonProperty("CardStatus")
        private String cardStatus;
        
        @JsonProperty("CardType")
        private String cardType;
        
        @JsonProperty("Expiry")
        private String expiry;
        
        @JsonProperty("BucketType")
        private String bucketType;
        
        @JsonProperty("Notes")
        private String notes;
        
        @JsonProperty("PayeeVPA")
        private String payeeVPA;
        
        @JsonProperty("PayerVPA")
        private String payerVPA;
        
        @JsonProperty("PayeeName")
        private String payeeName;
        
        @JsonProperty("PayerName")
        private String payerName;
        
        @JsonProperty("MccCode")
        private String mccCode;
        
        @JsonProperty("MccType")
        private String mccType;
        
        @JsonProperty("ResponseCode")
        private Integer responseCode;
        
        @JsonProperty("ResponseMessage")
        private String responseMessage;
    }
}