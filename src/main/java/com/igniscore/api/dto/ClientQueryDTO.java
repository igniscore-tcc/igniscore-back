package com.igniscore.api.dto;

import com.igniscore.api.model.Client;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * DTO responsible for encapsulating paginated {@link Client} query results.
 *
 * <p>This object is commonly used as a GraphQL response wrapper for
 * client listing operations, combining both the current page data
 * and pagination metadata in a single payload.
 *
 * <p><strong>Contained data:</strong>
 * <ul>
 *     <li>List of clients returned for the requested page</li>
 *     <li>Total number of available pages</li>
 *     <li>Total number of registered clients</li>
 * </ul>
 *
 * <p><strong>Usage context:</strong>
 * <ul>
 *     <li>GraphQL paginated queries</li>
 *     <li>REST pagination responses</li>
 *     <li>Frontend table/grid rendering</li>
 * </ul>
 */
public class ClientQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Current page content.
     */
    private final List<Client> clients;

    /**
     * Total number of pages available for the query.
     */
    private final int totalPages;

    /**
     * Total number of registered clients matching the query.
     */
    private final long totalClients;

    /**
     * Constructs a paginated client response DTO.
     *
     * @param clients current page content
     * @param totalPages total available pages
     * @param totalClients total number of matching clients
     */
    public ClientQueryDTO(
            List<Client> clients,
            int totalPages,
            long totalClients
    ) {
        this.clients = clients;
        this.totalPages = totalPages;
        this.totalClients = totalClients;
    }

    /**
     * Returns the current page client list.
     *
     * @return paginated clients
     */
    public List<Client> getClients() {
        return clients;
    }

    /**
     * Returns the total number of pages.
     *
     * @return total pages available
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Returns the total number of clients.
     *
     * @return total client count
     */
    public long getTotalClients() {
        return totalClients;
    }
}