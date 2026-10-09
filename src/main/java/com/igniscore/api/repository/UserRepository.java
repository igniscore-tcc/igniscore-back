package com.igniscore.api.repository;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Repository interface for {@link User} entity persistence.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations
 * and database interaction capabilities.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Handle persistence of User entities</li>
 *     <li>Provide query methods for user lookup</li>
 * </ul>
 *
 * <p>Notes:
 * <ul>
 *     <li>Email is used as a unique identifier for authentication</li>
 *     <li>The returned type {@link User} is expected to implement {@link UserDetails}</li>
 * </ul>
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Retrieves a user by email.
     *
     * <p>This method is primarily used during authentication
     * to load user details based on the provided username (email).
     *
     * @param email user email (unique identifier)
     * @return user entity if found, otherwise null
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.company WHERE u.email = :email")
    User findByEmail(String email);

    /**
     * Retrieves all users belonging to a company with pagination.
     *
     * @param company company used to filter the users
     * @param pageable pagination and sorting information
     * @return paginated list of users belonging to the company
     */
    @Query("""
        SELECT u
        FROM User u
        WHERE u.company = :company
          AND u.deletedAt IS NULL
        ORDER BY
            CASE WHEN u.role = com.igniscore.api.model.UserRole.OWNER THEN 0 ELSE 1 END,
            u.id ASC
        """)
    Page<User> findByCompanyAndDeletedAtIsNull(Company company, Pageable pageable);

    long countByCompanyAndDeletedAtIsNull(Company company);
}