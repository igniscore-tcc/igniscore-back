package com.igniscore.api.model;

/**
 * Enumeration representing the available types of products in the system.
 *
 * <p>This enum defines the classification of products managed by the platform,
 * allowing consistent categorization across persistence, business logic, and API layers.
 *
 * <p>Each enum constant is associated with a string representation used for
 * external communication (e.g., DTOs, GraphQL inputs, or database mapping if applicable).
 *
 * <p>Defined product types:
 * <ul>
 *     <li>{@link #EXTINGUISHER} - Represents fire extinguishers and related equipment</li>
 *     <li>{@link #SERVICE} - Represents service-based offerings (e.g., maintenance, inspection)</li>
 *     <li>{@link #CONSUMABLE} - Represents consumable items used in operations</li>
 *     <li>{@link #ACCESSORY} - Represents accessory items related to products or services</li>
 *     <li>{@link #HOSE} - Represents fire hoses and hose-related equipment</li>
 *     <li>{@link #DETECTOR} - Represents fire detection devices, including smoke and heat detectors</li>
 *     <li>{@link #SPRINKLER} - Represents sprinkler system components and related equipment</li>
 *     <li>{@link #CENTRAL} - Represents fire alarm control panels and central monitoring systems</li>
 *     <li>{@link #LIGHTING} - Represents emergency lighting systems and related components</li>
 *     <li>{@link #DOOR} - Represents fire-rated doors and associated safety door systems</li>
 *     <li>{@link #HYDRANT} - Represents fire hydrants and hydrant system components</li>
 * </ul>
 */
public enum ProductType {

    /**
     * Fire extinguisher products.
     */
    EXTINGUISHER("extinguisher"),

    /**
     * Service-based offerings such as maintenance or inspection.
     */
    SERVICE("service"),

    /**
     * Consumable products used in operational processes.
     */
    CONSUMABLE("consumable"),

    /**
     * Accessory products related to primary items.
     */
    ACCESSORY("accessory"),
    /**
     * Fire hose and hose-related equipment.
     */
    HOSE("hose"),

    /**
     * Fire detection devices, including smoke and heat detectors.
     */
    DETECTOR("detector"),

    /**
     * Sprinkler system components and related equipment.
     */
    SPRINKLER("sprinkler"),

    /**
     * Fire alarm control panels and central monitoring systems.
     */
    CENTRAL("central"),

    /**
     * Emergency lighting systems and related components.
     */
    LIGHTING("lighting"),

    /**
     * Fire-rated doors and associated safety door systems.
     */
    DOOR("door"),

    /**
     * Fire hydrants and hydrant system components.
     */
    HYDRANT("hydrant");
    /**
     * External string representation of the product type.
     */
    private final String type;

    /**
     * Constructs a ProductType with its associated string value.
     *
     * @param type external representation of the enum value
     */
    ProductType(String type) {
        this.type = type;
    }

    /**
     * Returns the external string representation of the product type.
     *
     * @return string value associated with the enum constant
     */
    public String getType() {
        return type;
    }
}