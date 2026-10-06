package com.igniscore.api.model;

/**
 * Enumeration representing the available sale types
 * supported by the sales system.
 *
 * <p>This enum is used to standardize sale type values
 * across the application domain, persistence layer,
 * and GraphQL API.
 *
 * <p>Supported sale types:
 * <ul>
 *     <li>{@link #INSTALLMENT} - Sale paid in installments.</li>
 *     <li>{@link #CASH} - Sale paid in full at the time of purchase.</li>
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
    CASH
}