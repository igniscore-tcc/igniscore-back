package com.igniscore.api.dto.client;

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
 *
 * @param clients      Current page content.
 * @param totalPages   Total number of pages available for the query.
 * @param totalClients Total number of registered clients matching the query.
 */
public record ClientQueryDTO(List<ClientResponseDTO> clients, int totalPages,
                             long totalClients) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}