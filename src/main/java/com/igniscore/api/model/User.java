package com.igniscore.api.model;

import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entity representing an application user.
 *
 * <p>This class implements {@link UserDetails} to integrate directly
 * with Spring Security authentication and authorization mechanisms.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Represents user data persisted in the database</li>
 *     <li>Provides authentication credentials (email, password)</li>
 *     <li>Defines authorization roles via {@link UserRole}</li>
 *     <li>Associates the user with a {@link Company} (multi-tenant context)</li>
 * </ul>
 *
 * <p>Security notes:
 * <ul>
 *     <li>Email is used as the unique username</li>
 *     <li>Authorities are derived from {@link UserRole}</li>
 *     <li>All account status flags currently return true (no restrictions enforced)</li>
 * </ul>
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Primary key identifier.
     */
    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_user")
    private Integer id;

    /**
     * User's full name.
     */
    @Column(name = "name_user", nullable = false)
    private String name;

    /**
     * User's email (used as unique identifier for authentication).
     */
    @Column(name = "email_user", nullable = false, unique = true)
    private String email;

    /**
     * User's hashed password.
     */
    @Column(name = "password_user", nullable = false)
    private String password;

    /**
     * User role used for authorization.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role_user", nullable = false)
    private UserRole role;

    /**
     * Associated company (multi-tenant relationship).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company")
    private Company company;

    // --- Getters ---

    public Integer getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    /**
     * Returns the user's password (used by Spring Security).
     */
    @Override
    public String getPassword() { return password; }

    /**
     * Returns the username used for authentication.
     * <p>In this system, email is used as the username.
     */
    @Override
    @NullMarked
    public String getUsername() { return email; }

    public UserRole getRole() { return role; }

    public Company getCompany() { return company; }

    // --- Setters ---

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setRole(UserRole role) { this.role = role; }

    public void setCompany(Company company) { this.company = company; }

    // --- Spring Security Methods ---

    /**
     * Returns the authorities granted to the user.
     *
     * <p>Each role is converted into a Spring Security authority
     * using the "ROLE_" prefix convention.
     *
     * @return collection of granted authorities
     */
    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true (no expiration logic implemented)
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Indicates whether the user is locked.
     *
     * @return true (no locking logic implemented)
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Indicates whether the user's credentials have expired.
     *
     * @return true (no credential expiration logic implemented)
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indicates whether the user is enabled.
     *
     * @return true (no enable/disable logic implemented)
     */
    @Override
    public boolean isEnabled() { return true; }
}