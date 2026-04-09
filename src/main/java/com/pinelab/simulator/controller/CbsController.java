package com.pinelab.simulator.controller;

import com.pinelab.simulator.dto.DebitRequest;
import com.pinelab.simulator.dto.CreditRequest;
import com.pinelab.simulator.dto.DebitReversalRequest;
import com.pinelab.simulator.dto.NcmcCreditRequest;
import com.pinelab.simulator.dto.NcmcValidateAddressRequest;
import com.pinelab.simulator.dto.NcmcCreditReversalRequest;
import com.pinelab.simulator.dto.CreditReversalRequest;
import com.pinelab.simulator.dto.MandateBlockFundRequest;
import com.pinelab.simulator.dto.MandateUnblockFundRequest;
import com.pinelab.simulator.dto.NcmcCheckStatusRequest;
import com.pinelab.simulator.service.CbsTransactionService;
import com.pinelab.simulator.service.IdempotencyService;
import com.pinelab.simulator.config.ResponseCodeConfig;
import com.pinelab.simulator.util.ResponseTemplateLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for all CBS transaction endpoints.
 * Account number is passed in URL path for validation.
 * 
 * Endpoint format: /api/{accountNumber}/<transaction-type>
 * Returns JSON string built from templates.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CbsController {
    
    private final CbsTransactionService transactionService;
    private final IdempotencyService idempotencyService;
    private final ResponseTemplateLoader templateLoader;
    private final ResponseCodeConfig responseCodeConfig;
    
    public CbsController(CbsTransactionService transactionService, 
                        IdempotencyService idempotencyService,
                        ResponseTemplateLoader templateLoader,
                        ResponseCodeConfig responseCodeConfig) {
        this.transactionService = transactionService;
        this.idempotencyService = idempotencyService;
        this.templateLoader = templateLoader;
        this.responseCodeConfig = responseCodeConfig;
    }
    
    /**
     * Build failure response for idempotency duplicate
     */
    private String buildIdempotencyFailureResponse(String apiName) {
        String responseCode = "00094";
        Integer responseCodeNum = 94;  // Maps from 00094
        String responseMessage = responseCodeConfig.getResponseMessage(responseCode);
        
        // Strip quotes if present in the message (from YAML config)
        if (responseMessage != null && responseMessage.startsWith("\"") && responseMessage.endsWith("\"")) {
            responseMessage = responseMessage.substring(1, responseMessage.length() - 1);
        }
        
        // Return simple JSON response with just ResponseCode and ResponseMessage
        return String.format("{\"ResponseCode\": %d, \"ResponseMessage\": \"%s\"}", 
                responseCodeNum, 
                responseMessage != null ? responseMessage : "Duplicate request with same idempotency key");
    }
    
    /**
     * Debit Transaction (Wallet Redeem) Endpoint
     * POST /api/{accountNumber}/debit
     */
    @PostMapping(value = "/{accountNumber}/debit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processDebit(
            @PathVariable String accountNumber,
            @RequestHeader(value = "TransactionId", required = false) Long transactionId,
            @RequestHeader(value = "DateAtClient", required = false) String dateAtClient,
            @RequestBody DebitRequest request) {
        
        log.info("Received Debit Request for account: {}, TransactionId: {}, DateAtClient: {}, payload: {}", 
                accountNumber, transactionId, dateAtClient, request);
        
        // Check idempotency
        if (idempotencyService.isDuplicate("debit", request.getIdempotencyKey())) {
            log.warn("Duplicate idempotency key for debit: {}", request.getIdempotencyKey());
            String failureResponse = buildIdempotencyFailureResponse("debit");
            return ResponseEntity.status(409).body(failureResponse);
        }
        
        String response = transactionService.processDebit(request, accountNumber, transactionId, dateAtClient);
        
        if (response == null) {
            return ResponseEntity.status(408).build();
        }
        
        // Store idempotency key after successful processing
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isEmpty()) {
            idempotencyService.store("debit", request.getIdempotencyKey());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CBS Simulator is running");
    }
    
    /**
     * Placeholder for other endpoints - will be implemented as per actual request/response format
     */
    
    /**
     * Credit Transaction Endpoint
     * POST /api/{accountNumber}/credit
     */
    @PostMapping(value = "/{accountNumber}/credit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processCredit(
            @PathVariable String accountNumber,
            @RequestHeader(value = "TransactionId", required = false) Long transactionId,
            @RequestHeader(value = "DateAtClient", required = false) String dateAtClient,
            @RequestBody CreditRequest request) {
        
        log.info("Received Credit Request for account: {}, payload: {}", accountNumber, request);
        
        // Check idempotency
        if (idempotencyService.isDuplicate("credit", request.getIdempotencyKey())) {
            log.warn("Duplicate idempotency key for credit: {}", request.getIdempotencyKey());
            String failureResponse = buildIdempotencyFailureResponse("credit");
            return ResponseEntity.status(409).body(failureResponse);
        }
        
        String response = transactionService.processCredit(request, accountNumber, transactionId, dateAtClient);
        
        if (response == null) {
            return ResponseEntity.status(408).build();
        }
        
        // Store idempotency key after successful processing
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isEmpty()) {
            idempotencyService.store("credit", request.getIdempotencyKey());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Debit Reversal Transaction Endpoint
     * POST /api/{accountNumber}/debit-reversal
     */
    @PostMapping(value = "/{accountNumber}/debit-reversal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processDebitReversal(
            @PathVariable String accountNumber,
            @RequestHeader(value = "TransactionId", required = false) Long transactionId,
            @RequestHeader(value = "DateAtClient", required = false) String dateAtClient,
            @RequestBody DebitReversalRequest request) {
        
        log.info("Received Debit Reversal Request for account: {}, TransactionId: {}, payload: {}", 
                accountNumber, transactionId, request);
        
        String response = transactionService.processDebitReversal(request, accountNumber, transactionId, dateAtClient);
        
        if (response == null) {
            return ResponseEntity.status(408).build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * NCMC Credit Transaction Endpoint
     * POST /api/ncmc-credit
     */
    @PostMapping(value = "/ncmc-credit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processNcmcCredit(
            @RequestHeader(value = "TransactionId", required = false) Long transactionId,
            @RequestHeader(value = "DateAtClient", required = false) String dateAtClient,
            @RequestBody NcmcCreditRequest request) {
        
        // Extract account number from request body
        String accountNumber = getAccountNumberFromRequest(request);
        
        log.info("Received NCMC Credit Request for account: {}, TransactionId: {}, payload: {}", 
                accountNumber, transactionId, request);
        
        // Check idempotency
        if (idempotencyService.isDuplicate("ncmc-credit", request.getIdempotencyKey())) {
            log.warn("Duplicate idempotency key for ncmc-credit: {}", request.getIdempotencyKey());
            String failureResponse = buildIdempotencyFailureResponse("ncmc-credit");
            return ResponseEntity.status(409).body(failureResponse);
        }
        
        String response = transactionService.processNcmcCredit(request, accountNumber, transactionId, dateAtClient);
        
        if (response == null) {
            return ResponseEntity.status(408).build();
        }
        
        // Store idempotency key after successful processing
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isEmpty()) {
            idempotencyService.store("ncmc-credit", request.getIdempotencyKey());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Credit Reversal Transaction Endpoint
     * POST /api/{accountNumber}/credit-reversal
     */
    @PostMapping("/{accountNumber}/credit-reversal")
    public ResponseEntity<Map<String, String>> processCreditReversal(
            @PathVariable String accountNumber,
            @RequestBody CreditReversalRequest request) {
        log.info("Received Credit Reversal Request for account: {}", accountNumber);
        return ResponseEntity.ok(Map.of("message", "Credit Reversal endpoint - to be implemented"));
    }
    
    /**
     * NCMC Validate Address Endpoint
     * POST /api/{accountNumber}/ncmc-validate-address
     */
    @PostMapping(value = "/{accountNumber}/ncmc-validate-address", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processNcmcValidateAddress(
            @PathVariable String accountNumber,
            @RequestHeader(value = "TransactionId", required = false) Long transactionId,
            @RequestHeader(value = "DateAtClient", required = false) String dateAtClient,
            @RequestBody NcmcValidateAddressRequest request) {
        
        log.info("Received NCMC Validate Address Request for account: {}, TransactionId: {}, payload: {}", 
                accountNumber, transactionId, request);
        
        String response = transactionService.processNcmcValidateAddress(request, accountNumber, transactionId, dateAtClient);
        
        if (response == null) {
            return ResponseEntity.status(408).build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * NCMC Credit Reversal Endpoint
     * POST /api/ncmc-credit-reversal
     */
    @PostMapping(value = "/ncmc-credit-reversal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processNcmcCreditReversal(
            @RequestHeader(value = "TransactionId", required = false) Long transactionId,
            @RequestHeader(value = "DateAtClient", required = false) String dateAtClient,
            @RequestBody NcmcCreditReversalRequest request) {
        
        // Extract account number from request body
        String accountNumber = getAccountNumberFromRequest(request);
        
        log.info("Received NCMC Credit Reversal Request for account: {}, TransactionId: {}, payload: {}", 
                accountNumber, transactionId, request);
        
        String response = transactionService.processNcmcCreditReversal(request, accountNumber, transactionId, dateAtClient);
        
        if (response == null) {
            return ResponseEntity.status(408).build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mandate Block Fund Endpoint
     * POST /api/{accountNumber}/mandate-block-fund
     */
    @PostMapping(value = "/{accountNumber}/mandate-block-fund", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processMandateBlockFund(
            @PathVariable String accountNumber,
            @RequestHeader(value = "TransactionId", required = false) Long transactionId,
            @RequestHeader(value = "DateAtClient", required = false) String dateAtClient,
            @RequestBody MandateBlockFundRequest request) {
        
        log.info("Received Mandate Block Fund Request for account: {}, TransactionId: {}, payload: {}", 
                accountNumber, transactionId, request);
        
        // Check idempotency
        if (idempotencyService.isDuplicate("mandate-block-fund", request.getIdempotencyKey())) {
            log.warn("Duplicate idempotency key for mandate-block-fund: {}", request.getIdempotencyKey());
            String failureResponse = buildIdempotencyFailureResponse("mandate-block-fund");
            return ResponseEntity.status(409).body(failureResponse);
        }
        
        String response = transactionService.processMandateBlockFund(request, accountNumber, transactionId, dateAtClient);
        
        if (response == null) {
            return ResponseEntity.status(408).build();
        }
        
        // Store idempotency key after successful processing
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isEmpty()) {
            idempotencyService.store("mandate-block-fund", request.getIdempotencyKey());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mandate Unblock Fund Endpoint
     * POST /api/{accountNumber}/mandate-unblock-fund
     */
    @PostMapping(value = "/{accountNumber}/mandate-unblock-fund", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processMandateUnblockFund(
            @PathVariable String accountNumber,
            @RequestHeader(value = "TransactionId", required = false) Long transactionId,
            @RequestHeader(value = "DateAtClient", required = false) String dateAtClient,
            @RequestBody MandateUnblockFundRequest request) {
        
        log.info("Received Mandate Unblock Fund Request for account: {}, TransactionId: {}, payload: {}", 
                accountNumber, transactionId, request);
        
        String response = transactionService.processMandateUnblockFund(request, accountNumber, transactionId, dateAtClient);
        
        if (response == null) {
            return ResponseEntity.status(408).build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * NCMC Check Status Endpoint
     * POST /api/ncmc-check-status
     */
    @PostMapping(value = "/ncmc-check-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processNcmcCheckStatus(
            @RequestHeader(value = "TransactionId", required = false) Long transactionId,
            @RequestHeader(value = "DateAtClient", required = false) String dateAtClient,
            @RequestBody NcmcCheckStatusRequest request) {
        
        // Extract account number from request body
        String accountNumber = getAccountNumberFromRequest(request);
        
        log.info("Received NCMC Check Status Request for account: {}, TransactionId: {}, payload: {}", 
                accountNumber, transactionId, request);
        
        // Check idempotency
        if (idempotencyService.isDuplicate("ncmc-check-status", request.getIdempotencyKey())) {
            log.warn("Duplicate idempotency key for ncmc-check-status: {}", request.getIdempotencyKey());
            String failureResponse = buildIdempotencyFailureResponse("ncmc-check-status");
            return ResponseEntity.status(409).body(failureResponse);
        }
        
        String response = transactionService.processNcmcCheckStatus(request, accountNumber, transactionId, dateAtClient);
        
        if (response == null) {
            return ResponseEntity.status(408).build();
        }
        
        // Store idempotency key after successful processing
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isEmpty()) {
            idempotencyService.store("ncmc-check-status", request.getIdempotencyKey());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Helper method to extract account number from NCMC request bodies.
     * Extracts from: 1) accounts[0].number, 2) walletNumber field
     */
    private String getAccountNumberFromRequest(NcmcCreditRequest request) {
        if (request != null) {
            // Try accounts[0].number first
            if (request.getAccounts() != null && !request.getAccounts().isEmpty()) {
                String number = request.getAccounts().get(0).getNumber();
                if (number != null && !number.isEmpty()) {
                    log.debug("Extracted account number from accounts[0].number: {}", number);
                    return number;
                }
            }
            // Fall back to walletNumber
            if (request.getWalletNumber() != null && !request.getWalletNumber().isEmpty()) {
                log.debug("Extracted account number from walletNumber: {}", request.getWalletNumber());
                return request.getWalletNumber();
            }
        }
        log.debug("No account number found in request, returning empty string");
        return "";
    }
    
    private String getAccountNumberFromRequest(NcmcCheckStatusRequest request) {
        if (request != null) {
            // Try accounts[0].number first
            if (request.getAccounts() != null && !request.getAccounts().isEmpty()) {
                String number = request.getAccounts().get(0).getNumber();
                if (number != null && !number.isEmpty()) {
                    log.debug("Extracted account number from accounts[0].number: {}", number);
                    return number;
                }
            }
            // Fall back to walletNumber
            if (request.getWalletNumber() != null && !request.getWalletNumber().isEmpty()) {
                log.debug("Extracted account number from walletNumber: {}", request.getWalletNumber());
                return request.getWalletNumber();
            }
        }
        return "";
    }
    
    private String getAccountNumberFromRequest(NcmcCreditReversalRequest request) {
        if (request != null) {
            // Try accounts[0].number first
            if (request.getAccounts() != null && !request.getAccounts().isEmpty()) {
                String number = request.getAccounts().get(0).getNumber();
                if (number != null && !number.isEmpty()) {
                    log.debug("Extracted account number from accounts[0].number: {}", number);
                    return number;
                }
            }
            // Fall back to walletNumber
            if (request.getWalletNumber() != null && !request.getWalletNumber().isEmpty()) {
                log.debug("Extracted account number from walletNumber: {}", request.getWalletNumber());
                return request.getWalletNumber();
            }
        }
        return "";
    }
}