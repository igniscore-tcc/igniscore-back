package com.igniscore.api.dto.sale;

import com.igniscore.api.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object responsible for receiving sale creation data
 * from GraphQL mutations.
 *
 * <p>This DTO represents the payload required to create a new sale,
 * including:
 * <ul>
 *     <li>The client associated with the sale</li>
 *     <li>The selected payment method</li>
 *     <li>The list of sale items</li>
 * </ul>
 *
 * <p>This object is automatically populated by Spring GraphQL
 * through the mutation input mapping process.
 *
 * <p><strong>GraphQL mapping:</strong>
 * <pre>
 * input CreateSaleInput {
 *     clientId: Int!
 *     paymentMethod: PaymentMethod!
 *     items: [CreateSaleItemInput!]!
 * }
 * </pre>
 */
public class CreateSaleDTO {

    /**
     * Identifier of the client associated with the sale.
     */
    private Integer clientId;

    /**
     * Payment method selected for the sale.
     */
    private PaymentMethod paymentMethod;

    private BigDecimal discount;

    /**
     * List of items included in the sale.
     */
    private List<CreateSaleItemDTO> items;

    /**
     * Returns the client identifier.
     *
     * @return client id
     */
    public Integer getClientId() {
        return clientId;
    }

    /**
     * Returns the selected payment method.
     *
     * @return payment method
     */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Returns the list of sale items.
     *
     * @return list of sale items
     */
    public List<CreateSaleItemDTO> getItems() {
        return items;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Sets the client identifier.
     *
     * @param clientId client id
     */
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod payment method
     */
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Sets the list of sale items.
     *
     * @param items sale items
     */
    public void setItems(List<CreateSaleItemDTO> items) {
        this.items = items;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}