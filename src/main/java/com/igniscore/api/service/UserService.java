package com.igniscore.api.service;

import com.igniscore.api.dto.user.ChangePasswordDTO;
import com.igniscore.api.dto.user.UserRegisterDTO;
import com.igniscore.api.dto.user.UserUpdateDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.model.UserRole;
import com.igniscore.api.repository.UserRepository;
import com.igniscore.api.service.subscription.RequiresSubscriptionAccess;
import com.igniscore.api.utils.CompanyUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@AllArgsConstructor
@Service
public class UserService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository repository;
    private final CompanyUtils companyUtils;
    private final AuthenticatedUserService authUserService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @RequiresSubscriptionAccess
    @Transactional
    public User updateUserCompany(String companyCnpj) {
        User user = authUserService.getUserOrThrow();
        Company company = companyUtils.existsCompany(companyCnpj);

        user.setCompany(company);
        user.setRole(UserRole.EMPLOYEE);

        return repository.save(user);
    }

    @RequiresSubscriptionAccess
    public User findUserId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    @RequiresSubscriptionAccess
    @Transactional
    public User update(String email, String name) {
        User user = repository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        user.setName(name);

        return repository.save(user);
    }

    @RequiresSubscriptionAccess
    public Page<User> findUsersByCompany(Pageable pageable) {
        User authenticatedUser = authUserService.getUserOrThrow();
        Company company = requireCompany(authenticatedUser);

        return repository.findByCompanyAndDeletedAtIsNull(company, pageable);
    }

    @RequiresSubscriptionAccess
    @Transactional
    public String store(UserRegisterDTO data) {
        User owner = authUserService.getUserOrThrow();

        if(owner.getRole() != UserRole.OWNER && owner.getCompany() != null) {
            return "It is not possible to create a user";
        }

        String temporaryPassword = generateTemporaryPassword();

        String encryptedPassword = new BCryptPasswordEncoder().encode(temporaryPassword);

        User user = new User(
                data.getName(),
                data.getEmail(),
                encryptedPassword,
                UserRole.EMPLOYEE,
                owner.getCompany());

        repository.save(user);

        emailService.sendTemporaryPassword(
                user.getEmail(),
                user.getName(),
                temporaryPassword
        );

        return "User successfully registered";

    }

    @RequiresSubscriptionAccess
    @Transactional
    public User completeOnboarding(Integer userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setOnboarding(true);

        return repository.save(user);
    }

    @RequiresSubscriptionAccess
    @Transactional
    public String changeTemporaryPassword(ChangePasswordDTO data) {
        User user = authUserService.getUserOrThrow();

        if (!user.isFirstLogin()) {
            return "Changing the temporary password is not necessary.";
        }

        String newPassword = data.getNewPassword();
        String confirmPassword = data.getConfirmPassword();

        if (newPassword == null || confirmPassword == null) {
            return "The new password and the confirmation are mandatory.";
        }

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            return "The password must be at least 8 characters long.";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "The passwords do not match.";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        user.setEmailVerified(true);

        repository.save(user);

        return "Password changed successfully.";
    }

    @RequiresSubscriptionAccess
    @Transactional
    public String updateEmployee(UserUpdateDTO data) {
        User employee = validateEmployee(data.getId());

        if (employee == null) {
            return "Unable to update employee.";
        }

        User existingUser = repository.findByEmail(data.getEmail());

        if (existingUser != null &&
                !Objects.equals(existingUser.getId(), employee.getId())) {
            return "Unable to update employee.";
        }

        employee.setName(data.getName());
        employee.setEmail(data.getEmail());

        repository.save(employee);

        return "Employee updated successfully.";
    }

    @RequiresSubscriptionAccess
    @Transactional
    public String deleteEmployee(Integer employeeId) {
        User employee = validateEmployee(employeeId);

        if (employee == null) {
            return "Unable to delete employee.";
        }

        employee.setDeletedAt(Timestamp.from(Instant.now()));

        repository.save(employee);

        return "Employee deleted successfully.";
    }

    private User validateEmployee(Integer employeeId) {
        User owner = authUserService.getUserOrThrow();

        if (owner.getRole() != UserRole.OWNER || owner.getCompany() == null) {
            return null;
        }

        User employee = repository.findById(employeeId).orElse(null);

        if (employee == null || employee.getDeletedAt() != null) {
            return null;
        }

        Company ownerCompany = owner.getCompany();
        Company employeeCompany = employee.getCompany();

        if (employeeCompany == null ||
                !Objects.equals(employeeCompany.getId(), ownerCompany.getId())) {
            return null;
        }

        if (employee.getRole() != UserRole.EMPLOYEE) {
            return null;
        }

        return employee;
    }

    private Company requireCompany(User user) {
        Company company = user.getCompany();

        if (company == null) {
            throw new RuntimeException("User is not associated with a company.");
        }

        return company;
    }

    private String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}