package com.igniscore.api.model;

/**
 * Enumeration representing the available payment methods
 * supported by the sales system.
 *
 * <p>This enum is used to standardize payment method values
 * across the application domain, persistence layer, and GraphQL API.
 *
 * <p>Each constant contains a normalized string representation
 * that can be used for serialization, logging, integrations,
 * or UI display purposes.
 *
 * <p>Supported payment methods:
 * <ul>
 *     <li>{@link #PIX} - Instant bank transfer payment</li>
 *     <li>{@link #CASH} - Physical cash payment</li>
 *     <li>{@link #CREDIT_CARD} - Credit card transaction</li>
 *     <li>{@link #DEBIT_CARD} - Debit card transaction</li>
 *     <li>{@link #BANK_SLIP} - Brazilian boleto bancário</li>
 * </ul>
 */
public enum PaymentMethod {

    /**
     * Instant payment using the Brazilian PIX system.
     */
    PIX("pix"),

    /**
     * Payment made using physical cash.
     */
    CASH("cash"),

    /**
     * Payment processed through a credit card.
     */
    CREDIT_CARD("credit_card"),

    /**
     * Payment processed through a debit card.
     */
    DEBIT_CARD("debit_card"),

    /**
     * Payment made using a bank slip (boleto bancário).
     */
    BANK_SLIP("bank_slip");

    /**
     * Normalized string representation of the payment method.
     */
    private final String method;

    /**
     * Creates a payment method enum constant.
     *
     * @param method normalized string value associated with the payment method
     */
    PaymentMethod(String method) {
        this.method = method;
    }

    /**
     * Returns the normalized string representation
     * of the payment method.
     *
     * @return payment method string value
     */
    public String getMethod() {
        return method;
    }
}