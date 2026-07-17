package com.igniscore.api.repository;

import com.igniscore.api.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByCode(String code);
    Optional<VerificationToken> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);
}

