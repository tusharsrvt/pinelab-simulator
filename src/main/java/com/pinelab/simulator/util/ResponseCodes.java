package com.pinelab.simulator.util;

/**
 * Response codes and their corresponding messages for CBS transactions.
 */
public final class ResponseCodes {
    
    private ResponseCodes() {}
    
    // Success codes
    public static final String SUCCESS = "0000";
    public static final String SUCCESS_MESSAGE = "Transaction Successful";
    
    // Failure codes
    public static final String INVALID_ACCOUNT = "U11";
    public static final String INVALID_ACCOUNT_MESSAGE = "Invalid Account Number";
    
    public static final String INSUFFICIENT_FUNDS = "U12";
    public static final String INSUFFICIENT_FUNDS_MESSAGE = "Insufficient Funds";
    
    public static final String INVALID_IFSC = "U13";
    public static final String INVALID_IFSC_MESSAGE = "Invalid IFSC Code";
    
    public static final String ACCOUNT_BLOCKED = "U14";
    public static final String ACCOUNT_BLOCKED_MESSAGE = "Account Blocked";
    
    public static final String INVALID_DETAILS = "U15";
    public static final String INVALID_DETAILS_MESSAGE = "Invalid Transaction Details";
    
    public static final String DUPLICATE_TRANSACTION = "U16";
    public static final String DUPLICATE_TRANSACTION_MESSAGE = "Duplicate Transaction";
    
    public static final String TIMEOUT = "U17";
    public static final String TIMEOUT_MESSAGE = "Transaction Timeout";
    
    public static final String SERVER_ERROR = "U18";
    public static final String SERVER_ERROR_MESSAGE = "Internal Server Error";
    
    public static final String INVALID_REQUEST = "U19";
    public static final String INVALID_REQUEST_MESSAGE = "Invalid Request";
    
    public static final String LIMIT_EXCEEDED = "U20";
    public static final String LIMIT_EXCEEDED_MESSAGE = "Transaction Limit Exceeded";
    
    public static final String MANDATE_NOT_FOUND = "U21";
    public static final String MANDATE_NOT_FOUND_MESSAGE = "Mandate Not Found";
    
    public static final String MANDATE_EXPIRED = "U22";
    public static final String MANDATE_EXPIRED_MESSAGE = "Mandate Expired";
    
    public static final String INVALID_MANDATE = "U23";
    public static final String INVALID_MANDATE_MESSAGE = "Invalid Mandate";
    
    public static final String TRANSACTION_NOT_FOUND = "U24";
    public static final String TRANSACTION_NOT_FOUND_MESSAGE = "Transaction Not Found";
    
    /**
     * Get response message for a given response code.
     */
    public static String getMessage(String respCode) {
        return switch (respCode) {
            case SUCCESS -> SUCCESS_MESSAGE;
            case INVALID_ACCOUNT -> INVALID_ACCOUNT_MESSAGE;
            case INSUFFICIENT_FUNDS -> INSUFFICIENT_FUNDS_MESSAGE;
            case INVALID_IFSC -> INVALID_IFSC_MESSAGE;
            case ACCOUNT_BLOCKED -> ACCOUNT_BLOCKED_MESSAGE;
            case INVALID_DETAILS -> INVALID_DETAILS_MESSAGE;
            case DUPLICATE_TRANSACTION -> DUPLICATE_TRANSACTION_MESSAGE;
            case TIMEOUT -> TIMEOUT_MESSAGE;
            case SERVER_ERROR -> SERVER_ERROR_MESSAGE;
            case INVALID_REQUEST -> INVALID_REQUEST_MESSAGE;
            case LIMIT_EXCEEDED -> LIMIT_EXCEEDED_MESSAGE;
            case MANDATE_NOT_FOUND -> MANDATE_NOT_FOUND_MESSAGE;
            case MANDATE_EXPIRED -> MANDATE_EXPIRED_MESSAGE;
            case INVALID_MANDATE -> INVALID_MANDATE_MESSAGE;
            case TRANSACTION_NOT_FOUND -> TRANSACTION_NOT_FOUND_MESSAGE;
            default -> "Unknown Response Code: " + respCode;
        };
    }
}