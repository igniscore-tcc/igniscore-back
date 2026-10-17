package com.igniscore.api.dto.sale;

import com.igniscore.api.model.SaleType;
import com.igniscore.api.model.SaleDocument;
import lombok.Getter;
import lombok.Setter;

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
@Setter
@Getter
public class CreateSaleDTO {

    /**
     * Identifier of the client associated with the sale.
     * -- GETTER --
     *  Returns the client identifier.
     * -- SETTER --
     *  Sets the client identifier.
     *
     */
    private Integer clientId;

    /**
     * Payment method selected for the sale.
     * -- GETTER --
     *  Returns the selected payment method.
     * -- SETTER --
     *  Sets the payment method.
     *
     */
    private SaleType paymentMethod;

    private BigDecimal discount;

    private SaleDocument type;

    private String document;

    /**
     * List of items included in the sale.
     * -- GETTER --
     *  Returns the list of sale items
     * -- SETTER --
     *  Sets the list of sale items.
     *
     */
    private List<CreateSaleItemDTO> items;

}