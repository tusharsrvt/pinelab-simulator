package com.pinelab.simulator.util;

import com.pinelab.simulator.config.SimulatorConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for matching account numbers against rules.
 * Supports exact match, wildcard/regex patterns, and numeric ranges.
 */
@Slf4j
public class RuleMatcher {

    /**
     * Match an account number against a list of rules.
     * Returns the first matching rule's config, or null if no match.
     *
     * @param last4DigitAccount The last 4 digits of the account number to match
     * @param rules List of rules to match against
     * @return The matched Rule's config (responseCode, responseTimeMs, timeout), or null
     */
    public static SimulatorConfig.Rule match(String last4DigitAccount, List<SimulatorConfig.Rule> rules) {
        if (last4DigitAccount == null || last4DigitAccount.isEmpty() || rules == null || rules.isEmpty()) {
            return null;
        }

        for (int i = 0; i < rules.size(); i++) {
            SimulatorConfig.Rule rule = rules.get(i);
            if (rule.getMatch() == null || rule.getMatch().isEmpty()) {
                continue;
            }

            for (String pattern : rule.getMatch()) {
                if (matches(last4DigitAccount, pattern)) {
                    log.debug("Account {} matched rule {} with pattern '{}'", last4DigitAccount, i, pattern);
                    return rule;
                }
            }
        }

        log.debug("No rules matched for account: {}", last4DigitAccount);
        return null;
    }

    /**
     * Check if an account number matches a specific pattern.
     * Pattern can be:
     * - Exact value: "1234"
     * - Wildcard/regex: "00.*" (treated as regex)
     * - Range: "0000-9999" or ["0000", "9999"]
     *
     * @param account The account number to match
     * @param pattern The pattern to match against
     * @return true if matched, false otherwise
     */
    public static boolean matches(String account, String pattern) {
        if (account == null || pattern == null) {
            return false;
        }

        // Check if it's a range pattern (contains "-")
        if (pattern.contains("-")) {
            return matchesRange(account, pattern);
        }

        // Check if it's a list format range ["0000", "9999"]
        // This is handled by the caller in the match method

        // Otherwise treat as exact match or regex
        // If pattern contains wildcards (* or ?), convert to regex
        if (pattern.contains(".*") || pattern.contains("%") || pattern.contains("?")) {
            return matchesRegex(account, pattern);
        }

        // Exact match (case insensitive for safety)
        return account.equalsIgnoreCase(pattern);
    }

    /**
     * Match against a range pattern like "0000-9999"
     */
    private static boolean matchesRange(String account, String pattern) {
        try {
            String[] parts = pattern.split("-");
            if (parts.length != 2) {
                return false;
            }

            String start = parts[0].trim();
            String end = parts[1].trim();

            // Account should be exactly the length of the range bounds
            if (account.length() != start.length() || account.length() != end.length()) {
                return false;
            }

            // Compare as strings (works for numeric strings with same length)
            return account.compareTo(start) >= 0 && account.compareTo(end) <= 0;
        } catch (Exception e) {
            log.warn("Error parsing range pattern '{}': {}", pattern, e.getMessage());
            return false;
        }
    }

    /**
     * Match using regex pattern.
     * Converts common wildcard patterns to regex:
     * - .* -> .*
     * - % -> .*
     * - ? -> .
     */
    private static boolean matchesRegex(String account, String pattern) {
        try {
            // Convert common wildcards to regex
            String regexPattern = pattern
                    .replace(".", "\\.")  // Escape dots (except wildcard)
                    .replace(".*", ".*")  // Keep wildcard
                    .replace("%", ".*")   // SQL-style wildcard
                    .replace("?", ".");   // Single char wildcard

            return Pattern.matches(regexPattern, account);
        } catch (Exception e) {
            log.warn("Error matching regex pattern '{}': {}", pattern, e.getMessage());
            return false;
        }
    }

    /**
     * Extract last 4 digits from full account number.
     *
     * @param accountNumber Full account number
     * @return Last 4 digits, or the input if less than 4 chars
     */
    public static String extractLast4Digits(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return "";
        }
        int length = accountNumber.length();
        return length >= 4 ? accountNumber.substring(length - 4) : accountNumber;
    }
}