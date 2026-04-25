package com.igniscore.api.service;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.UserRepository;
import com.igniscore.api.utils.CompanyUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing {@link User} entities.
 *
 * <p>This class handles user-related operations such as:
 * <ul>
 *     <li>Updating user-company associations</li>
 *     <li>Retrieving users by identifier</li>
 *     <li>Updating user profile data</li>
 * </ul>
 *
 * <p>Design considerations:
 * <ul>
 *     <li>All persistence operations are delegated to {@link UserRepository}</li>
 *     <li>Company validation is centralized via {@link CompanyUtils}</li>
 * </ul>
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final CompanyUtils companyUtils;
    private final AuthenticatedUserService authUserService;

    /**
     * Constructor-based dependency injection (preferred over field injection).
     *
     * @param repository   user persistence repository
     * @param companyUtils utility for company validation and retrieval
     */
    public UserService(UserRepository repository, CompanyUtils companyUtils, AuthenticatedUserService authUserService) {
        this.repository = repository;
        this.companyUtils = companyUtils;
        this.authUserService = authUserService;
    }

    /**
     * Updates the company associated with a user.
     *
     * <p>Flow:
     * <ol>
     *     <li>Retrieve user by ID</li>
     *     <li>Validate target company exists</li>
     *     <li>Update association</li>
     *     <li>Persist changes</li>
     * </ol>
     *
     * @param companyCnpj   target company identifier
     * @return updated user
     *
     * @throws RuntimeException if user or company is not found
     */
    public User updateUserCompany(String companyCnpj) {
        User user = this.authUserService.getUserOrThrow();

        Company company = companyUtils.existsCompany(companyCnpj);

        user.setCompany(company);

        return repository.save(user);
    }

    /**
     * Retrieves a user by its identifier.
     *
     * @param id user identifier
     * @return user entity
     *
     * @throws RuntimeException if user is not found
     */
    public User findUserId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    /**
     * Updates user profile information.
     *
     * <p>This method assumes email is used as a unique identifier.
     *
     * <p>Important:
     * This operation runs inside a transactional context,
     * so changes are automatically persisted without explicitly calling save().
     *
     * @param email user's email (identifier)
     * @param name  new name
     * @return updated user entity
     *
     * @throws RuntimeException if user is not found
     */
    @Transactional
    public User update(String email, String name) {
        User user = repository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        user.setName(name);
        user.setEmail(email);

        return user;
    }
}