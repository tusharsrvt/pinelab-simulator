package com.pinelab.simulator.service;

import com.pinelab.simulator.config.ConfigManager;
import com.pinelab.simulator.config.SimulatorConfig;
import com.pinelab.simulator.config.ResponseCodeConfig;
import com.pinelab.simulator.dto.DebitRequest;
import com.pinelab.simulator.dto.CreditRequest;
import com.pinelab.simulator.dto.DebitReversalRequest;
import com.pinelab.simulator.dto.NcmcCreditRequest;
import com.pinelab.simulator.dto.NcmcValidateAddressRequest;
import com.pinelab.simulator.dto.NcmcCreditReversalRequest;
import com.pinelab.simulator.dto.NcmcCheckStatusRequest;
import com.pinelab.simulator.dto.MandateBlockFundRequest;
import com.pinelab.simulator.dto.MandateUnblockFundRequest;
import com.pinelab.simulator.util.ResponseTemplateLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Service layer that handles CBS transaction processing.
 * Uses JSON templates to build responses.
 * 
 * Logic:
 * - Rules are defined in simulator-config.json per API
 * - First matching rule wins (exact, wildcard, or range)
 * - If no rules match, uses default config
 * 
 * DateTime handling:
 * - Uses dateAtClient from header if provided, otherwise falls back to server timestamp
 */
@Slf4j
@Service
public class CbsTransactionService {
    
    private final ConfigManager configManager;
    private final ResponseTemplateLoader templateLoader;
    private final ResponseCodeConfig responseCodeConfig;
    private final Random random = new Random();
    
    // Formatter for timestamps - compatible with LocalDateTime
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    public CbsTransactionService(ConfigManager configManager, ResponseTemplateLoader templateLoader, ResponseCodeConfig responseCodeConfig) {
        this.configManager = configManager;
        this.templateLoader = templateLoader;
        this.responseCodeConfig = responseCodeConfig;
    }
    
    /**
     * Process a debit (Wallet Redeem) transaction.
     */
    public String processDebit(DebitRequest request, String accountNumber, 
            Long transactionId, String dateAtClient) {
        
        String apiName = "debit";
        SimulatorConfig.ApiConfig config = getMatchingConfig(apiName, accountNumber);
        
        applyDelay(config.getResponseTimeMs());
        
        if (config.isTimeout()) {
            log.info("Simulating timeout for debit transaction with account: {}", accountNumber);
            return null;
        }
        
        return buildDebitResponse(apiName, request, accountNumber, transactionId, config, dateAtClient);
    }
    
    /**
     * Process a credit (Add Card to Wallet) transaction.
     */
    public String processCredit(CreditRequest request, String accountNumber, 
            Long transactionId, String dateAtClient) {
        
        String apiName = "credit";
        SimulatorConfig.ApiConfig config = getMatchingConfig(apiName, accountNumber);
        
        applyDelay(config.getResponseTimeMs());
        
        if (config.isTimeout()) {
            log.info("Simulating timeout for credit transaction with account: {}", accountNumber);
            return null;
        }
        
        return buildCreditResponse(apiName, request, accountNumber, transactionId, config, dateAtClient);
    }
    
    /**
     * Process a debit reversal transaction.
     */
    public String processDebitReversal(DebitReversalRequest request, String accountNumber, 
            Long transactionId, String dateAtClient) {
        
        String apiName = "debit-reversal";
        SimulatorConfig.ApiConfig config = getMatchingConfig(apiName, accountNumber);
        
        applyDelay(config.getResponseTimeMs());
        
        if (config.isTimeout()) {
            log.info("Simulating timeout for debit reversal transaction with account: {}", accountNumber);
            return null;
        }
        
        return buildDebitReversalResponse(apiName, request, accountNumber, transactionId, config, dateAtClient);
    }
    
    /**
     * Process an NCMC credit transaction.
     */
    public String processNcmcCredit(NcmcCreditRequest request, String accountNumber, 
            Long transactionId, String dateAtClient) {
        
        String apiName = "ncmc-credit";
        SimulatorConfig.ApiConfig config = getMatchingConfig(apiName, accountNumber);
        
        applyDelay(config.getResponseTimeMs());
        
        if (config.isTimeout()) {
            log.info("Simulating timeout for NCMC credit transaction with account: {}", accountNumber);
            return null;
        }
        
        return buildNcmcCreditResponse(apiName, request, accountNumber, transactionId, config, dateAtClient);
    }
    
    /**
     * Process an NCMC validate address transaction.
     */
    public String processNcmcValidateAddress(NcmcValidateAddressRequest request, String accountNumber, 
            Long transactionId, String dateAtClient) {
        
        String apiName = "ncmc-validate-address";
        SimulatorConfig.ApiConfig config = getMatchingConfig(apiName, accountNumber);
        
        applyDelay(config.getResponseTimeMs());
        
        if (config.isTimeout()) {
            log.info("Simulating timeout for NCMC validate address transaction with account: {}", accountNumber);
            return null;
        }
        
        return buildNcmcValidateAddressResponse(apiName, request, accountNumber, transactionId, config, dateAtClient);
    }
    
    /**
     * Process an NCMC credit reversal transaction.
     */
    public String processNcmcCreditReversal(NcmcCreditReversalRequest request, String accountNumber, 
            Long transactionId, String dateAtClient) {
        
        String apiName = "ncmc-credit-reversal";
        SimulatorConfig.ApiConfig config = getMatchingConfig(apiName, accountNumber);
        
        applyDelay(config.getResponseTimeMs());
        
        if (config.isTimeout()) {
            log.info("Simulating timeout for NCMC credit reversal transaction with account: {}", accountNumber);
            return null;
        }
        
        return buildNcmcCreditReversalResponse(apiName, request, accountNumber, transactionId, config, dateAtClient);
    }
    
    /**
     * Process an NCMC check status transaction.
     */
    public String processNcmcCheckStatus(NcmcCheckStatusRequest request, String accountNumber, 
            Long transactionId, String dateAtClient) {
        
        String apiName = "ncmc-check-status";
        SimulatorConfig.ApiConfig config = getMatchingConfig(apiName, accountNumber);
        
        applyDelay(config.getResponseTimeMs());
        
        if (config.isTimeout()) {
            log.info("Simulating timeout for NCMC check status transaction with account: {}", accountNumber);
            return null;
        }
        
        return buildNcmcCheckStatusResponse(apiName, request, accountNumber, transactionId, config, dateAtClient);
    }
    
    /**
     * Process a mandate block fund transaction.
     */
    public String processMandateBlockFund(MandateBlockFundRequest request, String accountNumber, 
            Long transactionId, String dateAtClient) {
        
        String apiName = "mandate-block-fund";
        SimulatorConfig.ApiConfig config = getMatchingConfig(apiName, accountNumber);
        
        applyDelay(config.getResponseTimeMs());
        
        if (config.isTimeout()) {
            log.info("Simulating timeout for mandate block fund transaction with account: {}", accountNumber);
            return null;
        }
        
        return buildMandateBlockFundResponse(apiName, request, accountNumber, transactionId, config, dateAtClient);
    }
    
    /**
     * Process a mandate unblock fund transaction.
     */
    public String processMandateUnblockFund(MandateUnblockFundRequest request, String accountNumber, 
            Long transactionId, String dateAtClient) {
        
        String apiName = "mandate-unblock-fund";
        SimulatorConfig.ApiConfig config = getMatchingConfig(apiName, accountNumber);
        
        applyDelay(config.getResponseTimeMs());
        
        if (config.isTimeout()) {
            log.info("Simulating timeout for mandate unblock fund transaction with account: {}", accountNumber);
            return null;
        }
        
        return buildMandateUnblockFundResponse(apiName, request, accountNumber, transactionId, config, dateAtClient);
    }
    
    /**
     * Get matching configuration based on account number from URL.
     * 
     * Rules:
     * Get matching config based on account number.
     * Uses rule-based matching if rules are defined, otherwise falls back to default behavior.
     */
    private SimulatorConfig.ApiConfig getMatchingConfig(String apiName, String accountNumber) {
        // Use ConfigManager's new rule-based matching
        SimulatorConfig.ApiConfig matchedConfig = configManager.getApiConfig(apiName, accountNumber);
        
        if (matchedConfig != null) {
            log.info("Matched config for API '{}' - responseCode: {}, responseTimeMs: {}, timeout: {}",
                    apiName, matchedConfig.getResponseCode(), matchedConfig.getResponseTimeMs(), matchedConfig.isTimeout());
        } else {
            log.info("No match found, using default config for API '{}'", apiName);
        }
        
        return matchedConfig;
    }
    
    /**
     * Apply configured delay.
     */
    private void applyDelay(Long delayMs) {
        if (delayMs != null && delayMs > 0) {
            try {
                log.info("Applying delay of {} ms", delayMs);
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Delay interrupted", e);
            }
        }
    }
    
    /**
     * Build debit response using template.
     */
    private String buildDebitResponse(String apiName, DebitRequest request, String accountNumber,
            Long transactionId, SimulatorConfig.ApiConfig config, String dateAtClient) {
        
        // Determine if success or failure based on response code
        boolean isSuccess = "0000".equals(config.getResponseCode());
        
        Integer responseCode = isSuccess ? 0 : mapResponseCode(config.getResponseCode());
        String responseMessage = isSuccess ? "Transaction successful." : getFailureMessage(config.getResponseCode());
        
        // Generate batch number
        long batchNumber = 16000000L + random.nextInt(1000000);
        
        // Generate approval code
        String approvalCode = isSuccess ? String.format("%012d", Math.abs(random.nextLong()) % 1000000000000L) : null;
        
        // Use dateAtClient from header if provided, otherwise use server timestamp
        String timestamp = dateAtClient != null && !dateAtClient.isEmpty() ? dateAtClient : 
                LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        // Build placeholder values map
        Map<String, Object> values = new HashMap<>();
        
        // Common values
        values.put("WALLET_NUMBER", accountNumber);
        values.put("TRANSACTION_ID", transactionId != null ? transactionId : 0);
        values.put("DATE_AT_SERVER", timestamp);
        values.put("CURRENT_BATCH_NUMBER", batchNumber);
        values.put("BATCH_NUMBER", batchNumber);
        values.put("RESPONSE_CODE", responseCode);
        values.put("RESPONSE_MESSAGE", responseMessage);
        values.put("ERROR_CODE", isSuccess ? null : config.getResponseCode());
        values.put("ERROR_DESCRIPTION", isSuccess ? null : responseMessage);
        
        // Debit-specific values
        if (request != null) {
            values.put("AMOUNT", request.getAmount() != null ? request.getAmount() : 0);
            values.put("BILL_AMOUNT", request.getBillAmount() != null ? request.getBillAmount() : 0);
            values.put("INVOICE_NUMBER", request.getInvoiceNumber() != null ? request.getInvoiceNumber() : "");
            values.put("NOTES", request.getNotes() != null ? request.getNotes() : "");
            values.put("PAYEE_VPA", request.getPayeeVPA() != null ? request.getPayeeVPA() : "");
            values.put("PAYER_VPA", request.getPayerVPA() != null ? request.getPayerVPA() : "");
            values.put("PAYEE_NAME", request.getPayeeName() != null ? request.getPayeeName() : "");
            values.put("PAYER_NAME", request.getPayerName() != null ? request.getPayerName() : "");
            values.put("MCC_CODE", request.getMccCode() != null ? request.getMccCode() : "");
            values.put("MCC_TYPE", request.getMccType() != null ? request.getMccType() : "");
            values.put("UPI_TXN_TYPE", request.getUpiTxnType() != null ? request.getUpiTxnType() : "");
        } else {
            values.put("AMOUNT", 0);
            values.put("BILL_AMOUNT", 0);
            values.put("INVOICE_NUMBER", "");
            values.put("NOTES", "");
            values.put("PAYEE_VPA", "");
            values.put("PAYER_VPA", "");
            values.put("PAYEE_NAME", "");
            values.put("PAYER_NAME", "");
            values.put("MCC_CODE", "");
            values.put("MCC_TYPE", "");
            values.put("UPI_TXN_TYPE", "");
        }
        
        // Card-specific values (only for success)
        if (isSuccess) {
            values.put("CARD_NUMBER", "730001" + String.format("%07d", random.nextInt(10000000)));
            values.put("CARD_BALANCE", 103460.52 + random.nextDouble() * 1000);
            values.put("EXPIRY", LocalDateTime.now().plusYears(8).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
            values.put("BALANCE", 103460.52 + random.nextDouble() * 1000);
            values.put("CONSOLIDATED_BALANCE", 103460.52 + random.nextDouble() * 1000);
            values.put("TRANSACTION_TYPE", "WALLET REDEEM");
        } else {
            values.put("CARD_NUMBER", null);
            values.put("CARD_BALANCE", null);
            values.put("EXPIRY", null);
            values.put("BALANCE", null);
            values.put("CONSOLIDATED_BALANCE", null);
            values.put("TRANSACTION_TYPE", null);
        }
        
        values.put("APPROVAL_CODE", approvalCode);
        
        // Fill template
        return templateLoader.fillTemplate(apiName, values);
    }
    
    /**
     * Map config response codes to integer values.
     * 0000 = success (0)
     * Other codes map to failure codes (e.g., U15 -> 15, U16 -> 16)
     */
    private Integer mapResponseCode(String respCode) {
        if (respCode == null || respCode.isEmpty() || "0000".equals(respCode)) {
            return 0;
        }
        String numeric = respCode.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(numeric);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
    
    /**
     * Get failure message based on response code from config.
     */
    private String getFailureMessage(String respCode) {
        String message = responseCodeConfig.getResponseMessage(respCode);
        // Strip quotes if present in the message (from YAML config)
        if (message != null && message.startsWith("\"") && message.endsWith("\"")) {
            message = message.substring(1, message.length() - 1);
        }
        return message;
    }
    
    /**
     * Build credit response using template.
     */
    private String buildCreditResponse(String apiName, CreditRequest request, String accountNumber,
            Long transactionId, SimulatorConfig.ApiConfig config, String dateAtClient) {
        
        boolean isSuccess = "0000".equals(config.getResponseCode());
        
        Integer responseCode = isSuccess ? 0 : mapResponseCode(config.getResponseCode());
        String responseMessage = isSuccess ? "Transaction successful." : getFailureMessage(config.getResponseCode());
        
        long batchNumber = 16000000L + random.nextInt(1000000);
        String approvalCode = isSuccess ? String.format("%012d", Math.abs(random.nextLong()) % 1000000000000L) : null;
        double walletBalance = 103460.52 + random.nextDouble() * 1000;
        
        // Use dateAtClient from header if provided, otherwise use server timestamp
        String timestamp = dateAtClient != null && !dateAtClient.isEmpty() ? dateAtClient : 
                LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        Map<String, Object> values = new HashMap<>();
        
        values.put("WALLET_NUMBER", accountNumber);
        values.put("TRANSACTION_ID", transactionId != null ? transactionId : 0);
        values.put("DATE_AT_SERVER", timestamp);
        values.put("CURRENT_BATCH_NUMBER", batchNumber);
        values.put("BATCH_NUMBER", batchNumber);
        values.put("RESPONSE_CODE", responseCode);
        values.put("RESPONSE_MESSAGE", responseMessage);
        values.put("ERROR_CODE", isSuccess ? null : config.getResponseCode());
        values.put("ERROR_DESCRIPTION", isSuccess ? null : responseMessage);
        
        if (request != null) {
            values.put("AMOUNT", request.getAmount() != null ? request.getAmount() : 0);
            values.put("BILL_AMOUNT", 0);
            values.put("INVOICE_NUMBER", request.getInvoiceNumber() != null ? request.getInvoiceNumber() : null);
            values.put("NOTES", request.getNotes() != null ? request.getNotes() : "");
            values.put("PAYEE_VPA", request.getPayeeVPA() != null ? request.getPayeeVPA() : "");
            values.put("PAYER_VPA", request.getPayerVPA() != null ? request.getPayerVPA() : "");
            values.put("PAYEE_NAME", request.getPayeeName() != null ? request.getPayeeName() : "");
            values.put("PAYER_NAME", request.getPayerName() != null ? request.getPayerName() : "");
            values.put("MCC_CODE", request.getMccCode() != null ? request.getMccCode() : "");
            values.put("MCC_TYPE", request.getMccType() != null ? request.getMccType() : "");
            values.put("UPI_TXN_TYPE", request.getUpiTxnType() != null ? request.getUpiTxnType() : "");
            values.put("CARD_PROGRAM_NAME", request.getCardProgramName() != null ? request.getCardProgramName() : "PL Fave Money PPI Reloadable CPG");
        } else {
            values.put("AMOUNT", 0);
            values.put("BILL_AMOUNT", 0);
            values.put("INVOICE_NUMBER", null);
            values.put("NOTES", "");
            values.put("PAYEE_VPA", "");
            values.put("PAYER_VPA", "");
            values.put("PAYEE_NAME", "");
            values.put("PAYER_NAME", "");
            values.put("MCC_CODE", "");
            values.put("MCC_TYPE", "");
            values.put("UPI_TXN_TYPE", "");
            values.put("CARD_PROGRAM_NAME", "PL Fave Money PPI Reloadable CPG");
        }
        
        if (isSuccess) {
            values.put("EXTERNAL_WALLET_ID", "+91" + accountNumber.substring(accountNumber.length() - 10));
            values.put("WALLET_HOLDER_NAME", "NOT SPECIFIED. " + (request != null && request.getPayeeName() != null ? request.getPayeeName() : "USER") + " LASTNAME");
            values.put("WALLET_BALANCE", walletBalance);
            values.put("CONSOLIDATED_BALANCE", walletBalance);
            values.put("CARD_NUMBER", "730001" + String.format("%07d", random.nextInt(10000000)));
            values.put("EXPIRY", LocalDateTime.now().plusYears(8).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
            values.put("TRANSACTION_TYPE", "ADD CARD TO WALLET");
            values.put("APPROVAL_CODE", approvalCode);
            values.put("SALUTATION", "NOT SPECIFIED");
            values.put("FIRST_NAME", request != null && request.getPayeeName() != null ? request.getPayeeName().toUpperCase() : "USER");
            values.put("LAST_NAME", "LASTNAME");
            values.put("PHONE_NUMBER", accountNumber.substring(accountNumber.length() - 10));
            values.put("EMAIL", "user@email.com");
            values.put("DOB", "01010001");
        } else {
            values.put("EXTERNAL_WALLET_ID", null);
            values.put("WALLET_HOLDER_NAME", null);
            values.put("WALLET_BALANCE", null);
            values.put("CONSOLIDATED_BALANCE", null);
            values.put("CARD_NUMBER", null);
            values.put("EXPIRY", null);
            values.put("TRANSACTION_TYPE", null);
            values.put("APPROVAL_CODE", null);
            values.put("SALUTATION", null);
            values.put("FIRST_NAME", null);
            values.put("LAST_NAME", null);
            values.put("PHONE_NUMBER", null);
            values.put("EMAIL", null);
            values.put("DOB", null);
        }
        
        return templateLoader.fillTemplate(apiName, values);
    }
    
    /**
     * Build debit reversal response using template.
     */
    private String buildDebitReversalResponse(String apiName, DebitReversalRequest request, String accountNumber,
            Long transactionId, SimulatorConfig.ApiConfig config, String dateAtClient) {
        
        boolean isSuccess = "0000".equals(config.getResponseCode());
        
        Integer responseCode = isSuccess ? 0 : mapResponseCode(config.getResponseCode());
        String responseMessage = isSuccess ? "Transaction successful." : getFailureMessage(config.getResponseCode());
        
        long batchNumber = 16000000L + random.nextInt(1000000);
        String approvalCode = isSuccess ? String.format("%012d", Math.abs(random.nextLong()) % 1000000000000L) : null;
        double cardBalance = 103460.52 + random.nextDouble() * 1000;
        
        Map<String, Object> values = new HashMap<>();
        
        // Common values
        values.put("WALLET_NUMBER", accountNumber);
        values.put("TRANSACTION_ID", transactionId != null ? transactionId : 0);
        values.put("CURRENT_BATCH_NUMBER", batchNumber);
        values.put("BATCH_NUMBER", batchNumber);
        values.put("RESPONSE_CODE", responseCode);
        values.put("RESPONSE_MESSAGE", responseMessage);
        values.put("ERROR_CODE", isSuccess ? null : config.getResponseCode());
        values.put("ERROR_DESCRIPTION", isSuccess ? null : responseMessage);
        
        // Debit Reversal specific values
        if (request != null) {
            values.put("AMOUNT", 0); // No amount field in request, default to 0
            values.put("INVOICE_NUMBER", request.getOriginalTransactionId() != null ? request.getOriginalTransactionId() : "");
            values.put("NOTES", request.getNotes() != null ? request.getNotes() : "");
            values.put("PAYER_NAME", request.getPayerName() != null ? request.getPayerName() : "");
            values.put("MCC_CODE", request.getMccCode() != null ? request.getMccCode() : "");
            values.put("MCC_TYPE", request.getMccType() != null ? request.getMccType() : "");
            values.put("UPI_TXN_TYPE", "");
        } else {
            values.put("AMOUNT", 0);
            values.put("INVOICE_NUMBER", "");
            values.put("NOTES", "");
            values.put("PAYER_NAME", "");
            values.put("MCC_CODE", "");
            values.put("MCC_TYPE", "");
            values.put("UPI_TXN_TYPE", "");
        }
        
        // Card-specific values (only for success)
        if (isSuccess) {
            values.put("CARD_NUMBER", "730001" + String.format("%07d", random.nextInt(10000000)));
            values.put("CARD_BALANCE", cardBalance);
            values.put("EXPIRY", LocalDateTime.now().plusYears(8).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
            values.put("BALANCE", cardBalance);
            values.put("CONSOLIDATED_BALANCE", cardBalance);
            values.put("CARD_PROGRAM_NAME", "PL Fave Money PPI Reloadable CPG");
            values.put("TRANSACTION_TYPE", "DEBIT REVERSAL");
            values.put("APPROVAL_CODE", approvalCode);
        } else {
            values.put("CARD_NUMBER", null);
            values.put("CARD_BALANCE", null);
            values.put("EXPIRY", null);
            values.put("BALANCE", null);
            values.put("CONSOLIDATED_BALANCE", null);
            values.put("CARD_PROGRAM_NAME", null);
            values.put("TRANSACTION_TYPE", null);
            values.put("APPROVAL_CODE", null);
        }
        
        // Use dateAtClient from header if provided, otherwise use server timestamp
        String timestamp = dateAtClient != null && !dateAtClient.isEmpty() ? dateAtClient : 
                LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        values.put("DATE_AT_SERVER", timestamp);
        
        return templateLoader.fillTemplate(apiName, values);
    }
    
    /**
     * Build NCMC credit response using template.
     */
    private String buildNcmcCreditResponse(String apiName, NcmcCreditRequest request, String accountNumber,
            Long transactionId, SimulatorConfig.ApiConfig config, String dateAtClient) {
        
        boolean isSuccess = "0000".equals(config.getResponseCode());
        
        Integer responseCode = isSuccess ? 0 : mapResponseCode(config.getResponseCode());
        String responseMessage = isSuccess ? "Transaction successful." : getFailureMessage(config.getResponseCode());
        
        long batchNumber = 16000000L + random.nextInt(1000000);
        String approvalCode = isSuccess ? String.format("%012d", Math.abs(random.nextLong()) % 1000000000000L) : null;
        
        // Generate dynamic values
        double balance = 1000 + random.nextDouble() * 500;
        double preXactionBalance = balance + (request != null && request.getInvoiceAmount() != null ? request.getInvoiceAmount() : 32);
        
        // Use dateAtClient from header if provided, otherwise use server timestamp
        String timestamp = dateAtClient != null && !dateAtClient.isEmpty() ? dateAtClient : 
                LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        // Transaction datetime for response
        String transactionDateTime = timestamp;
        
        // Timestamps (expiry and activation dates still use local formatter)
        String expiryDate = LocalDateTime.now().plusYears(8).format(TIMESTAMP_FORMATTER);
        String activationDate = LocalDateTime.now().minusMonths(2).format(TIMESTAMP_FORMATTER);
        
        // Extract account details from request
        String accountNumberFromReq = accountNumber;
        String payerName = "";
        String payerVpa = "";
        double transactionAmount = 0;
        String invoiceNumber = "";
        String notes = "";
        String idempotencyKey = "";
        
        if (request != null && request.getAccounts() != null && !request.getAccounts().isEmpty()) {
            NcmcCreditRequest.Account acc = request.getAccounts().get(0);
            accountNumberFromReq = acc.getNumber() != null ? acc.getNumber() : accountNumber;
            payerName = acc.getPayerName() != null ? acc.getPayerName() : "";
            payerVpa = acc.getPayerVpa() != null ? acc.getPayerVpa() : "";
            try {
                transactionAmount = acc.getAmount() != null ? Double.parseDouble(acc.getAmount()) : 0;
            } catch (NumberFormatException e) {
                transactionAmount = 0;
            }
        }
        if (request != null) {
            invoiceNumber = request.getInvoiceNumber() != null ? request.getInvoiceNumber() : "";
            notes = request.getNotes() != null ? request.getNotes() : "";
            idempotencyKey = request.getIdempotencyKey() != null ? request.getIdempotencyKey() : "";
        }
        
        Map<String, Object> values = new HashMap<>();
        
        // Top-level response
        values.put("RESPONSE_CODE", responseCode);
        values.put("RESPONSE_MESSAGE", responseMessage);
        values.put("ERROR_CODE", isSuccess ? null : config.getResponseCode());
        values.put("ERROR_DESCRIPTION", isSuccess ? null : responseMessage);
        values.put("APPROVAL_CODE_TOP", approvalCode);
        values.put("TOTAL_ACCOUNTS", 1);
        values.put("TOTAL_AMOUNT", transactionAmount);
        values.put("NUMBER_OF_ACCOUNTS", 1);
        values.put("MERCHANT_OUTLET_NAME", "Woohoo UPI Corporate - Fave");
        values.put("TRANSACTION_ID", transactionId != null ? transactionId : 0);
        values.put("NOTES", notes);
        values.put("CURRENT_BATCH_NUMBER", batchNumber);
        values.put("DESCRIPTIVE_OUTLET_NAME", "Woohoo UPI Corporate");
        values.put("IDEMPOTENCY_KEY", idempotencyKey);
        
        // Account level values
        if (isSuccess) {
            values.put("ISSUER_NAME", "PL_Yuva Pay");
            values.put("NATIVE_ACCOUNT_BALANCE", (int) balance);
            values.put("NATIVE_CURRENCY_CODE", "INR");
            values.put("PRE_XACTION_ACCOUNT_BALANCE", (int) preXactionBalance);
            values.put("PRE_XACTION_ACCOUNT_BALANCE_NATIVE", String.format("%.4f", preXactionBalance));
            values.put("TOTAL_REDEEMED_AMOUNT", 0);
            values.put("TRANSACTION_AMOUNT", (int) transactionAmount);
            values.put("TRANSACTION_DATE_TIME", transactionDateTime);
            values.put("PAYER_NAME", payerName);
            values.put("EXPIRY_DATE", expiryDate);
            values.put("ACCOUNT_CREATION_TYPE", "Virtual");
            values.put("ACCOUNT_ISSUING_MODE", "STORE");
            values.put("ACCOUNT_TYPE", "Yuva Pay PPI MTS");
            values.put("ACTIVATION_DATE", activationDate);
            values.put("FIRST_NAME", "RIYA");
            values.put("LAST_NAME", "RIYA");
            values.put("PAYER_VPA", payerVpa);
            values.put("PPI_POCKET_BALANCE", 0);
            values.put("NCMC_PENDING_UPDATE_AMOUNT", (int) (preXactionBalance - balance));
            values.put("NCMC_POCKET_BALANCE", (int) balance);
            values.put("PRE_XACTION_BALANCE_SYMBOL", "₹ " + String.format("%,.4f", preXactionBalance));
            values.put("REUSABLE", true);
            values.put("TOTAL_PRE_AUTH_AMOUNT", 0);
            values.put("TOTAL_RELOADED_AMOUNT", 0);
            values.put("TRANSFERABLE", true);
            values.put("APPROVAL_CODE", approvalCode);
            values.put("ACCOUNT_NUMBER", accountNumberFromReq);
            values.put("TRANSFER_ACCOUNT_BALANCE", 0);
            values.put("ACCOUNT_BEHAVIOUR_TYPE_ID", 0);
            values.put("ACCOUNT_CURRENCY_SYMBOL", "₹");
            values.put("ACCOUNT_NATIVE_BALANCE", String.format("%.4f", balance));
            values.put("ACCOUNT_STATUS", "Activated");
            values.put("ACCOUNT_STATUS_ID", 140);
            values.put("ACTIVATION_AMOUNT", 0);
            values.put("ADJUSTMENT_AMOUNT", 0);
            values.put("BALANCE", (int) balance);
            values.put("PROGRAM_GROUP_TYPE", "PPI Program");
            values.put("CURRENCY_CONVERSION_RATE", 1);
            values.put("CURRENCY_CONVERTED_XACTION_AMOUNT", (int) transactionAmount);
            values.put("CURRENCY_CODE", "INR");
            values.put("INVOICE_NUMBER", invoiceNumber);
        } else {
            // Failure response - null values for account details
            values.put("ISSUER_NAME", null);
            values.put("NATIVE_ACCOUNT_BALANCE", null);
            values.put("NATIVE_CURRENCY_CODE", null);
            values.put("PRE_XACTION_ACCOUNT_BALANCE", null);
            values.put("PRE_XACTION_ACCOUNT_BALANCE_NATIVE", null);
            values.put("TOTAL_REDEEMED_AMOUNT", null);
            values.put("TRANSACTION_AMOUNT", null);
            values.put("TRANSACTION_DATE_TIME", null);
            values.put("PAYER_NAME", null);
            values.put("EXPIRY_DATE", null);
            values.put("ACCOUNT_CREATION_TYPE", null);
            values.put("ACCOUNT_ISSUING_MODE", null);
            values.put("ACCOUNT_TYPE", null);
            values.put("ACTIVATION_DATE", null);
            values.put("FIRST_NAME", null);
            values.put("LAST_NAME", null);
            values.put("PAYER_VPA", null);
            values.put("PPI_POCKET_BALANCE", null);
            values.put("NCMC_PENDING_UPDATE_AMOUNT", null);
            values.put("NCMC_POCKET_BALANCE", null);
            values.put("PRE_XACTION_BALANCE_SYMBOL", null);
            values.put("REUSABLE", null);
            values.put("TOTAL_PRE_AUTH_AMOUNT", null);
            values.put("TOTAL_RELOADED_AMOUNT", null);
            values.put("TRANSFERABLE", null);
            values.put("APPROVAL_CODE", null);
            values.put("ACCOUNT_NUMBER", null);
            values.put("TRANSFER_ACCOUNT_BALANCE", null);
            values.put("ACCOUNT_BEHAVIOUR_TYPE_ID", null);
            values.put("ACCOUNT_CURRENCY_SYMBOL", null);
            values.put("ACCOUNT_NATIVE_BALANCE", null);
            values.put("ACCOUNT_STATUS", null);
            values.put("ACCOUNT_STATUS_ID", null);
            values.put("ACTIVATION_AMOUNT", null);
            values.put("ADJUSTMENT_AMOUNT", null);
            values.put("BALANCE", null);
            values.put("PROGRAM_GROUP_TYPE", null);
            values.put("CURRENCY_CONVERSION_RATE", null);
            values.put("CURRENCY_CONVERTED_XACTION_AMOUNT", null);
            values.put("CURRENCY_CODE", null);
            values.put("INVOICE_NUMBER", null);
        }
        
        return templateLoader.fillTemplate(apiName, values);
    }
    
    /**
     * Build NCMC validate address response using template.
     */
    private String buildNcmcValidateAddressResponse(String apiName, NcmcValidateAddressRequest request, 
            String accountNumber, Long transactionId, SimulatorConfig.ApiConfig config, String dateAtClient) {
        
        boolean isSuccess = "0000".equals(config.getResponseCode());
        
        Integer responseCode = isSuccess ? 0 : mapResponseCode(config.getResponseCode());
        String responseMessage = isSuccess ? "Success" : getFailureMessage(config.getResponseCode());
        
        // Extract VPA from request
        String vpa = request != null && request.getVpa() != null ? request.getVpa() : "";
        
        Map<String, Object> values = new HashMap<>();
        
        values.put("RESPONSE_CODE", responseCode);
        
        if (isSuccess) {
            // Generate customer name from VPA or use default
            String customerName = "Riya Jain";
            if (!vpa.isEmpty() && vpa.contains(".")) {
                // Extract identifier from VPA and create a name
                String[] parts = vpa.split("@")[0].split("\\.");
                if (parts.length >= 2) {
                    String firstName = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1);
                    customerName = firstName + " Jain";
                }
            }
            values.put("CUSTOMER_NAME", customerName);
            values.put("ACCOUNT_NUMBER", accountNumber);
        } else {
            values.put("CUSTOMER_NAME", null);
            values.put("ACCOUNT_NUMBER", null);
        }
        
        values.put("RESPONSE_MESSAGE", responseMessage);
        values.put("VPA", vpa);
        
        return templateLoader.fillTemplate(apiName, values);
    }
    
    /**
     * Build NCMC credit reversal response using template.
     */
    private String buildNcmcCreditReversalResponse(String apiName, NcmcCreditReversalRequest request, 
            String accountNumber, Long transactionId, SimulatorConfig.ApiConfig config, String dateAtClient) {
        
        boolean isSuccess = "0000".equals(config.getResponseCode());
        
        Integer responseCode = isSuccess ? 0 : mapResponseCode(config.getResponseCode());
        String responseMessage = isSuccess ? "Transaction successful." : getFailureMessage(config.getResponseCode());
        
        long batchNumber = 16000000L + random.nextInt(1000000);
        // Use originalBatchNumber from request if provided
        if (request != null && request.getOriginalBatchNumber() != null) {
            batchNumber = request.getOriginalBatchNumber();
        }
        
        String approvalCode = isSuccess ? String.format("%012d", Math.abs(random.nextLong()) % 1000000000000L) : null;
        
        // Extract account details from request
        String accountNumberFromReq = accountNumber;
        double transactionAmount = 0;
        String invoiceNumber = "";
        
        if (request != null && request.getAccounts() != null && !request.getAccounts().isEmpty()) {
            NcmcCreditReversalRequest.Account acc = request.getAccounts().get(0);
            accountNumberFromReq = acc.getNumber() != null ? acc.getNumber() : accountNumber;
            transactionAmount = acc.getAmount() != null ? acc.getAmount() : 0;
        }
        if (request != null) {
            invoiceNumber = request.getInvoiceNumber() != null ? request.getInvoiceNumber() : "";
        }
        
        // Dynamic balance calculation (add back the reversed amount for reversal)
        double balance = 950 + random.nextDouble() * 100;
        double preXactionBalance = balance + transactionAmount;
        
        // Timestamps
        String transactionDateTime = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String expiryDate = LocalDateTime.now().plusYears(8).format(TIMESTAMP_FORMATTER);
        String activationDate = LocalDateTime.now().minusMonths(2).format(TIMESTAMP_FORMATTER);
        
        Map<String, Object> values = new HashMap<>();
        
        // Top-level response
        values.put("RESPONSE_CODE_TOP", responseCode);
        values.put("NUMBER_OF_ACCOUNTS", 1);
        values.put("TOTAL_ACCOUNTS", 1);
        values.put("APPROVAL_CODE_TOP", approvalCode);
        values.put("CURRENT_BATCH_NUMBER", batchNumber);
        values.put("RESPONSE_MESSAGE", responseMessage);
        values.put("TOTAL_AMOUNT", (int) transactionAmount);
        values.put("TRANSACTION_ID", transactionId != null ? transactionId : 0);
        
        // Account level values
        if (isSuccess) {
            values.put("ISSUER_NAME", "PL_Yuva Pay");
            values.put("PROGRAM_GROUP_TYPE", "PPI Program");
            values.put("ACCOUNT_CURRENCY_SYMBOL", "₹");
            values.put("ACCOUNT_NUMBER", accountNumberFromReq);
            values.put("PRE_XACTION_ACCOUNT_BALANCE", (int) preXactionBalance);
            values.put("ACCOUNT_STATUS", "Activated");
            values.put("ACCOUNT_STATUS_ID", 140);
            values.put("ACCOUNT_TYPE", "Yuva Pay PPI MTS");
            values.put("ACTIVATION_AMOUNT", 0);
            values.put("FIRST_NAME", "RIYA");
            values.put("LAST_NAME", "RIYA");
            values.put("ACCOUNT_BEHAVIOUR_TYPE_ID", 0);
            values.put("ACCOUNT_CREATION_TYPE", "Virtual");
            values.put("ACCOUNT_ISSUING_MODE", "STORE");
            values.put("ACCOUNT_NATIVE_BALANCE", String.format("%.4f", balance));
            values.put("ACTIVATION_DATE", activationDate);
            values.put("CURRENCY_CONVERTED_XACTION_AMOUNT", (int) transactionAmount);
            values.put("APPROVAL_CODE", approvalCode);
            values.put("BALANCE", (int) balance);
            values.put("CURRENCY_CODE", "INR");
            values.put("CURRENCY_CONVERSION_RATE", 1);
            values.put("EXPIRY_DATE", expiryDate);
            values.put("INVOICE_NUMBER", invoiceNumber);
            values.put("PRE_XACTION_BALANCE_NATIVE", String.format("%.4f", preXactionBalance));
            values.put("NATIVE_ACCOUNT_BALANCE", (int) balance);
            values.put("PPI_POCKET_BALANCE", 0);
            values.put("NCMC_PENDING_UPDATE_AMOUNT", 0);
            values.put("NCMC_POCKET_BALANCE", (int) balance);
            values.put("PRE_XACTION_BALANCE_SYMBOL", "₹ " + String.format("%,.4f", preXactionBalance));
            values.put("REUSABLE", true);
            values.put("TOTAL_PRE_AUTH_AMOUNT", 0);
            values.put("TOTAL_RELOADED_AMOUNT", 0);
            values.put("TRANSFERABLE", true);
            values.put("TRANSFER_ACCOUNT_BALANCE", 0);
            values.put("NATIVE_CURRENCY_CODE", "INR");
            values.put("ADJUSTMENT_AMOUNT", 0);
            values.put("TRANSACTION_AMOUNT", (int) transactionAmount);
            values.put("TOTAL_REDEEMED_AMOUNT", 0);
            values.put("RESPONSE_CODE", responseCode);
            values.put("TRANSACTION_DATE_TIME", transactionDateTime);
        } else {
            // Failure response - null values
            values.put("ISSUER_NAME", null);
            values.put("PROGRAM_GROUP_TYPE", null);
            values.put("ACCOUNT_CURRENCY_SYMBOL", null);
            values.put("ACCOUNT_NUMBER", null);
            values.put("PRE_XACTION_ACCOUNT_BALANCE", null);
            values.put("ACCOUNT_STATUS", null);
            values.put("ACCOUNT_STATUS_ID", null);
            values.put("ACCOUNT_TYPE", null);
            values.put("ACTIVATION_AMOUNT", null);
            values.put("FIRST_NAME", null);
            values.put("LAST_NAME", null);
            values.put("ACCOUNT_BEHAVIOUR_TYPE_ID", null);
            values.put("ACCOUNT_CREATION_TYPE", null);
            values.put("ACCOUNT_ISSUING_MODE", null);
            values.put("ACCOUNT_NATIVE_BALANCE", null);
            values.put("ACTIVATION_DATE", null);
            values.put("CURRENCY_CONVERTED_XACTION_AMOUNT", null);
            values.put("APPROVAL_CODE", null);
            values.put("BALANCE", null);
            values.put("CURRENCY_CODE", null);
            values.put("CURRENCY_CONVERSION_RATE", null);
            values.put("EXPIRY_DATE", null);
            values.put("INVOICE_NUMBER", null);
            values.put("PRE_XACTION_BALANCE_NATIVE", null);
            values.put("NATIVE_ACCOUNT_BALANCE", null);
            values.put("PPI_POCKET_BALANCE", null);
            values.put("NCMC_PENDING_UPDATE_AMOUNT", null);
            values.put("NCMC_POCKET_BALANCE", null);
            values.put("PRE_XACTION_BALANCE_SYMBOL", null);
            values.put("REUSABLE", null);
            values.put("TOTAL_PRE_AUTH_AMOUNT", null);
            values.put("TOTAL_RELOADED_AMOUNT", null);
            values.put("TRANSFERABLE", null);
            values.put("TRANSFER_ACCOUNT_BALANCE", null);
            values.put("NATIVE_CURRENCY_CODE", null);
            values.put("ADJUSTMENT_AMOUNT", null);
            values.put("TRANSACTION_AMOUNT", null);
            values.put("TOTAL_REDEEMED_AMOUNT", null);
            values.put("RESPONSE_CODE", null);
            values.put("TRANSACTION_DATE_TIME", null);
        }
        
        return templateLoader.fillTemplate(apiName, values);
    }
    
    /**
     * Build NCMC check status response using template.
     */
    private String buildNcmcCheckStatusResponse(String apiName, NcmcCheckStatusRequest request, 
            String accountNumber, Long transactionId, SimulatorConfig.ApiConfig config, String dateAtClient) {
        
        boolean isSuccess = "0000".equals(config.getResponseCode());
        
        Integer responseCode = isSuccess ? 0 : mapResponseCode(config.getResponseCode());
        String responseMessage = isSuccess ? "Transaction successful." : getFailureMessage(config.getResponseCode());
        
        long batchNumber = 16000000L + random.nextInt(1000000);
        String approvalCode = isSuccess ? String.format("%012d", Math.abs(random.nextLong()) % 1000000000000L) : null;
        
        // Extract account details from request
        String accountNumberFromReq = accountNumber;
        String payerName = "";
        String payerVpa = "";
        double transactionAmount = 0;
        String invoiceNumber = "";
        String notes = "";
        String idempotencyKey = "";
        
        if (request != null && request.getAccounts() != null && !request.getAccounts().isEmpty()) {
            NcmcCheckStatusRequest.Account acc = request.getAccounts().get(0);
            accountNumberFromReq = acc.getNumber() != null ? acc.getNumber() : accountNumber;
            payerName = acc.getPayerName() != null ? acc.getPayerName() : "";
            payerVpa = acc.getPayerVpa() != null ? acc.getPayerVpa() : "";
            try {
                transactionAmount = acc.getAmount() != null ? Double.parseDouble(acc.getAmount()) : 0;
            } catch (NumberFormatException e) {
                transactionAmount = 0;
            }
        }
        if (request != null) {
            invoiceNumber = request.getInvoiceNumber() != null ? request.getInvoiceNumber() : "";
            notes = request.getNotes() != null ? request.getNotes() : "";
            idempotencyKey = request.getIdempotencyKey() != null ? request.getIdempotencyKey() : "";
        }
        
        // Dynamic balance calculation
        double balance = 250 + random.nextDouble() * 50;
        double preXactionBalance = balance + transactionAmount;
        
        // Timestamps
        String transactionDateTime = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String expiryDate = LocalDateTime.now().plusYears(8).format(TIMESTAMP_FORMATTER);
        String activationDate = LocalDateTime.now().minusMonths(2).format(TIMESTAMP_FORMATTER);
        
        Map<String, Object> values = new HashMap<>();
        
        // Top-level response
        values.put("NUMBER_OF_ACCOUNTS", 1);
        values.put("TOTAL_ACCOUNTS", 1);
        values.put("TOTAL_AMOUNT", (int) transactionAmount);
        values.put("TRANSACTION_ID", transactionId != null ? transactionId : 0);
        values.put("APPROVAL_CODE_TOP", approvalCode);
        values.put("RESPONSE_CODE_TOP", responseCode);
        values.put("RESPONSE_MESSAGE", responseMessage);
        values.put("NOTES_TOP", notes);
        values.put("CURRENT_BATCH_NUMBER", batchNumber);
        values.put("IDEMPOTENCY_KEY", idempotencyKey);
        
        // Account level values
        if (isSuccess) {
            values.put("PRE_XACTION_BALANCE_NATIVE", String.format("%.4f", preXactionBalance));
            values.put("PRE_XACTION_BALANCE_SYMBOL", "₹ " + String.format("%,.4f", preXactionBalance));
            values.put("PROGRAM_GROUP_TYPE", "PPI Program");
            values.put("REUSABLE", true);
            values.put("APPROVAL_CODE", approvalCode);
            values.put("TOTAL_REDEEMED_AMOUNT", 0);
            values.put("TOTAL_RELOADED_AMOUNT", 0);
            values.put("TRANSACTION_AMOUNT", (int) transactionAmount);
            values.put("TRANSACTION_DATE_TIME", transactionDateTime);
            values.put("TRANSFER_ACCOUNT_BALANCE", 0);
            values.put("TRANSFERABLE", true);
            values.put("ACCOUNT_NUMBER", accountNumberFromReq);
            values.put("TOTAL_PRE_AUTH_AMOUNT", 0);
            values.put("RESPONSE_CODE", responseCode);
            values.put("NOTES", notes);
            values.put("ACCOUNT_BEHAVIOUR_TYPE_ID", 0);
            values.put("ACCOUNT_CREATION_TYPE", "Virtual");
            values.put("ACCOUNT_CURRENCY_SYMBOL", "₹");
            values.put("ACCOUNT_ISSUING_MODE", "STORE");
            values.put("ACCOUNT_NATIVE_BALANCE", String.format("%.4f", balance));
            values.put("ACCOUNT_STATUS", "Activated");
            values.put("ACCOUNT_STATUS_ID", 140);
            values.put("ACCOUNT_TYPE", "Yuva Pay PPI MTS");
            values.put("ACTIVATION_AMOUNT", 0);
            values.put("ACTIVATION_DATE", activationDate);
            values.put("ADJUSTMENT_AMOUNT", 0);
            values.put("BALANCE", (int) balance);
            values.put("CURRENCY_CODE", "INR");
            values.put("CURRENCY_CONVERSION_RATE", 1);
            values.put("CURRENCY_CONVERTED_XACTION_AMOUNT", (int) transactionAmount);
            values.put("FIRST_NAME", "RIYA");
            values.put("LAST_NAME", "RIYA");
            values.put("EXPIRY_DATE", expiryDate);
            values.put("INVOICE_NUMBER", invoiceNumber);
            values.put("ISSUER_NAME", "PL_Yuva Pay");
            values.put("NATIVE_ACCOUNT_BALANCE", (int) balance);
            values.put("NATIVE_CURRENCY_CODE", "INR");
            values.put("PAYER_NAME", payerName);
            values.put("PAYER_VPA", payerVpa);
            values.put("PPI_POCKET_BALANCE", 0);
            values.put("NCMC_POCKET_BALANCE", (int) balance);
            values.put("NCMC_PENDING_UPDATE_AMOUNT", (int) transactionAmount);
            values.put("PRE_XACTION_ACCOUNT_BALANCE", (int) preXactionBalance);
        } else {
            // Failure response - null values
            values.put("PRE_XACTION_BALANCE_NATIVE", null);
            values.put("PRE_XACTION_BALANCE_SYMBOL", null);
            values.put("PROGRAM_GROUP_TYPE", null);
            values.put("REUSABLE", null);
            values.put("APPROVAL_CODE", null);
            values.put("TOTAL_REDEEMED_AMOUNT", null);
            values.put("TOTAL_RELOADED_AMOUNT", null);
            values.put("TRANSACTION_AMOUNT", null);
            values.put("TRANSACTION_DATE_TIME", null);
            values.put("TRANSFER_ACCOUNT_BALANCE", null);
            values.put("TRANSFERABLE", null);
            values.put("ACCOUNT_NUMBER", null);
            values.put("TOTAL_PRE_AUTH_AMOUNT", null);
            values.put("RESPONSE_CODE", null);
            values.put("NOTES", null);
            values.put("ACCOUNT_BEHAVIOUR_TYPE_ID", null);
            values.put("ACCOUNT_CREATION_TYPE", null);
            values.put("ACCOUNT_CURRENCY_SYMBOL", null);
            values.put("ACCOUNT_ISSUING_MODE", null);
            values.put("ACCOUNT_NATIVE_BALANCE", null);
            values.put("ACCOUNT_STATUS", null);
            values.put("ACCOUNT_STATUS_ID", null);
            values.put("ACCOUNT_TYPE", null);
            values.put("ACTIVATION_AMOUNT", null);
            values.put("ACTIVATION_DATE", null);
            values.put("ADJUSTMENT_AMOUNT", null);
            values.put("BALANCE", null);
            values.put("CURRENCY_CODE", null);
            values.put("CURRENCY_CONVERSION_RATE", null);
            values.put("CURRENCY_CONVERTED_XACTION_AMOUNT", null);
            values.put("FIRST_NAME", null);
            values.put("LAST_NAME", null);
            values.put("EXPIRY_DATE", null);
            values.put("INVOICE_NUMBER", null);
            values.put("ISSUER_NAME", null);
            values.put("NATIVE_ACCOUNT_BALANCE", null);
            values.put("NATIVE_CURRENCY_CODE", null);
            values.put("PAYER_NAME", null);
            values.put("PAYER_VPA", null);
            values.put("PPI_POCKET_BALANCE", null);
            values.put("NCMC_POCKET_BALANCE", null);
            values.put("NCMC_PENDING_UPDATE_AMOUNT", null);
            values.put("PRE_XACTION_ACCOUNT_BALANCE", null);
        }
        
        return templateLoader.fillTemplate(apiName, values);
    }
    
    /**
     * Build mandate block fund response using template.
     */
    private String buildMandateBlockFundResponse(String apiName, MandateBlockFundRequest request, 
            String accountNumber, Long transactionId, SimulatorConfig.ApiConfig config, String dateAtClient) {
        
        boolean isSuccess = "0000".equals(config.getResponseCode());
        
        Integer responseCode = isSuccess ? 0 : mapResponseCode(config.getResponseCode());
        String responseMessage = isSuccess ? "Transaction successful." : getFailureMessage(config.getResponseCode());
        
        long batchNumber = 16000000L + random.nextInt(1000000);
        String approvalCode = isSuccess ? String.format("%012d", Math.abs(random.nextLong()) % 1000000000000L) : null;
        String preAuthCode = isSuccess ? String.format("%010d", Math.abs(random.nextLong()) % 10000000000L) : null;
        
        // Extract values from request
        double amount = 0;
        String invoiceNumber = "";
        String payerVpa = "";
        String payerName = "";
        String payeeVpa = "";
        String payeeName = "";
        String mccCode = "";
        String mccType = "";
        String upiTxnType = "";
        
        if (request != null) {
            try {
                amount = request.getAmount() != null ? Double.parseDouble(request.getAmount()) : 0;
            } catch (NumberFormatException e) {
                amount = 0;
            }
            invoiceNumber = request.getInvoiceNumber() != null ? request.getInvoiceNumber() : "";
            payerVpa = request.getPayerVPA() != null ? request.getPayerVPA() : "";
            payerName = request.getPayerName() != null ? request.getPayerName() : "";
            payeeVpa = request.getPayeeVPA() != null ? request.getPayeeVPA() : "";
            payeeName = request.getPayeeName() != null ? request.getPayeeName() : "";
            mccCode = request.getMccCode() != null ? request.getMccCode() : "";
            mccType = request.getMccType() != null ? request.getMccType() : "";
            upiTxnType = request.getUpiTxnType() != null ? request.getUpiTxnType() : "";
        }
        
        // Use dateAtClient from header if provided, otherwise use server timestamp
        String timestamp = dateAtClient != null && !dateAtClient.isEmpty() ? dateAtClient : 
                LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        double balance = 50 + random.nextDouble() * 50;
        
        // Timestamps
        String expiryDate = LocalDateTime.now().plusYears(8).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        
        Map<String, Object> values = new HashMap<>();
        
        if (isSuccess) {
            values.put("CARD_NUMBER", "730001" + String.format("%07d", random.nextInt(10000000)));
            values.put("AMOUNT", (int) amount);
            values.put("CARD_BALANCE", balance);
            values.put("CARD_PROGRAM_NAME", "PL Fave Money PPI Reloadable CPG");
            values.put("CARD_STATUS", "Activated");
            values.put("CARD_TYPE", "Virtual");
            values.put("EXPIRY", expiryDate);
            values.put("PAYEE_VPA", payeeVpa);
            values.put("PAYER_VPA", payerVpa);
            values.put("PAYEE_NAME", payeeName);
            values.put("PAYER_NAME", payerName);
            values.put("MCC_CODE", mccCode);
            values.put("MCC_TYPE", mccType);
            values.put("RESPONSE_CODE", responseCode);
            values.put("RESPONSE_MESSAGE", responseMessage);
        } else {
            values.put("CARD_NUMBER", null);
            values.put("AMOUNT", null);
            values.put("CARD_BALANCE", null);
            values.put("CARD_PROGRAM_NAME", null);
            values.put("CARD_STATUS", null);
            values.put("CARD_TYPE", null);
            values.put("EXPIRY", null);
            values.put("PAYEE_VPA", null);
            values.put("PAYER_VPA", null);
            values.put("PAYEE_NAME", null);
            values.put("PAYER_NAME", null);
            values.put("MCC_CODE", null);
            values.put("MCC_TYPE", null);
            values.put("RESPONSE_CODE", responseCode);
            values.put("RESPONSE_MESSAGE", responseMessage);
        }
        
        values.put("CURRENT_BATCH_NUMBER", batchNumber);
        values.put("WALLET_NUMBER", accountNumber);
        values.put("INVOICE_NUMBER", invoiceNumber);
        values.put("DATE_AT_SERVER", timestamp);
        values.put("BATCH_NUMBER", batchNumber);
        values.put("BALANCE", isSuccess ? balance : 0);
        values.put("CONSOLIDATED_BALANCE", isSuccess ? balance : 0);
        values.put("BILL_AMOUNT", (int) amount);
        values.put("UPI_TXN_TYPE", upiTxnType);
        values.put("APPROVAL_CODE", approvalCode);
        values.put("TRANSACTION_ID", transactionId != null ? transactionId : 0);
        values.put("TRANSACTION_TYPE", "WALLET REDEEM");
        values.put("ERROR_CODE", isSuccess ? null : config.getResponseCode());
        values.put("ERROR_DESCRIPTION", isSuccess ? null : responseMessage);
        values.put("PRE_AUTH_CODE", preAuthCode);
        
        return templateLoader.fillTemplate(apiName, values);
    }
    
    /**
     * Build mandate unblock fund response using template.
     */
    private String buildMandateUnblockFundResponse(String apiName, MandateUnblockFundRequest request, 
            String accountNumber, Long transactionId, SimulatorConfig.ApiConfig config, String dateAtClient) {
        
        boolean isSuccess = "0000".equals(config.getResponseCode());
        
        Integer responseCode = isSuccess ? 0 : mapResponseCode(config.getResponseCode());
        String responseMessage = isSuccess ? "Transaction successful." : getFailureMessage(config.getResponseCode());
        
        long batchNumber = 16000000L + random.nextInt(1000000);
        String approvalCode = isSuccess ? String.format("%012d", Math.abs(random.nextLong()) % 1000000000000L) : null;
        
        // Use dateAtClient from header if provided, otherwise use server timestamp
        String timestamp = dateAtClient != null && !dateAtClient.isEmpty() ? dateAtClient : 
                LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        Map<String, Object> values = new HashMap<>();
        
        values.put("CURRENT_BATCH_NUMBER", batchNumber);
        values.put("WALLET_NUMBER", accountNumber);
        values.put("DATE_AT_SERVER", timestamp);
        values.put("BATCH_NUMBER", batchNumber);
        values.put("AMOUNT", isSuccess ? 0 : 0);
        values.put("BALANCE", isSuccess ? 0 : 0);
        values.put("CONSOLIDATED_BALANCE", isSuccess ? 0 : 0);
        values.put("APPROVAL_CODE", approvalCode);
        values.put("RESPONSE_CODE", responseCode);
        values.put("RESPONSE_MESSAGE", responseMessage);
        values.put("TRANSACTION_ID", transactionId != null ? transactionId : 0);
        values.put("TRANSACTION_TYPE", "WALLET CANCEL REDEEM");
        values.put("ERROR_CODE", isSuccess ? null : config.getResponseCode());
        values.put("ERROR_DESCRIPTION", isSuccess ? null : responseMessage);
        
        return templateLoader.fillTemplate(apiName, values);
    }
}