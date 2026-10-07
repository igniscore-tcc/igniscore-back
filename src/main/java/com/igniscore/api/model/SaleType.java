package com.igniscore.api.model;

/**
 * Enumeration representing the available payment methods
 * supported by the sales system.
 *
 * <p>This enum is used to standardize payment method values
 * across the application domain, persistence layer,
 * and GraphQL API.
 *
 * <p>Supported payment methods:
 * <ul>
 *     <li>{@link #INSTALLMENT} - Sale paid in installments.</li>
 *     <li>{@link #CASH} - Sale paid in full at the time of purchase.</li>
 *     <li>{@link #CREDIT_CARD} - Payment made by credit card.</li>
 *     <li>{@link #DEBIT_CARD} - Payment made by debit card.</li>
 *     <li>{@link #PIX} - Payment made via Pix.</li>
 *     <li>{@link #BANK_SLIP} - Payment made via bank slip.</li>
 * </ul>
 */
public enum SaleType {

    /**
     * Sale paid in installments.
     */
    INSTALLMENT,

    /**
     * Sale paid in full at the time of purchase.
     */
    CASH,

    /**
     * Payment made by credit card.
     */
    CREDIT_CARD,

    /**
     * Payment made by debit card.
     */
    DEBIT_CARD,

    /**
     * Payment made via Pix.
     */
    PIX,

    /**
     * Payment made via bank slip.
     */
    BANK_SLIP
}