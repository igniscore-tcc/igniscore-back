package com.igniscore.api.service;

import com.igniscore.api.dto.auth.LoginResponseDTO;
import com.igniscore.api.dto.auth.RegisterDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.model.UserRole;
import com.igniscore.api.model.VerificationToken;
import com.igniscore.api.repository.UserRepository;
import com.igniscore.api.repository.VerificationTokenRepository;
import com.igniscore.api.utils.CompanyUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

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
    private final JwtService jwtService;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final TokenGeneratorService tokenGenerator;

    /**
     * Constructor-based dependency injection (preferred over field injection).
     *
     * @param repository   user persistence repository
     * @param companyUtils utility for company validation and retrieval
     */
    public UserService(UserRepository repository,
                       CompanyUtils companyUtils,
                       AuthenticatedUserService authUserService,
                       JwtService jwtService,
                       EmailService emailService,
                       VerificationTokenRepository verificationTokenRepository,
                       TokenGeneratorService tokenGenerator) {
        this.repository = repository;
        this.companyUtils = companyUtils;
        this.authUserService = authUserService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.tokenGenerator = tokenGenerator;
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
        user.setRole(UserRole.EMPLOYEE);

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

    /**
     * Retrieves all users belonging to the authenticated user's company.
     *
     * @param pageable pagination and sorting information
     * @return paginated list of users belonging to the company
     */
    public Page<User> findUsersByCompany(Pageable pageable) {
        User authenticatedUser = authUserService.getUserOrThrow();

        Company company = authenticatedUser.getCompany();

        if (company == null) {
            throw new RuntimeException("User is not associated with a company.");
        }

        return repository.findByCompany(company, pageable);
    }

    @Transactional
    public String store(RegisterDTO data) {

        User owner = authUserService.getUserOrThrow();

        if (owner.getRole() != UserRole.OWNER) {
            return "Apenas o proprietário pode cadastrar funcionários.";
        }

        Company company = owner.getCompany();

        if (company == null) {
            return "O proprietário não possui uma empresa associada.";
        }

        if (repository.findByEmail(data.email()) != null) {
            return "Erro ao criar conta.";
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        User newUser = new User();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.EMPLOYEE);
        newUser.setActive(true);
        newUser.setEmailVerified(false);
        newUser.setCompany(company);

        User savedUser = repository.save(newUser);

        String code = tokenGenerator.generateVerificationCode();

        VerificationToken verificationToken = new VerificationToken(
                code,
                savedUser,
                LocalDateTime.now().plusMinutes(15)
        );

        verificationTokenRepository.save(verificationToken);

        emailService.sendVerificationCode(
                savedUser.getEmail(),
                code
        );

        return "Funcionário criado com sucesso. Um e-mail de verificação foi enviado para o funcionário.";
    }
}