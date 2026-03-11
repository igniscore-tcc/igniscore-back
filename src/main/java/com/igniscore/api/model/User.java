package com.igniscore.api.model;

import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_user")
    private Integer id;

    @Column(name = "name_user", nullable = false)
    private String name;

    @Column(name = "email_user", nullable = false, unique = true)
    private String email;

    @Column(name = "password_user", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_user", nullable = false)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company")
    private Company company;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    @Override
    public String getPassword() { return password; }
    @Override
    @NullMarked
    public String getUsername() { return email; }
    public UserRole getRole() { return role; }
    public Company getCompany() { return company; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(UserRole role) { this.role = role; }
    public void setCompany(Company company) { this.company = company; }

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}