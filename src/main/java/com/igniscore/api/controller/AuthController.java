package com.igniscore.api.controller;

import com.igniscore.api.dto.auth.AutDTO;
import com.igniscore.api.dto.auth.RegisterDTO;
import com.igniscore.api.dto.auth.LoginResponseDTO;
import com.igniscore.api.dto.auth.VerifyCodeDTO;
import com.igniscore.api.model.PasswordResetToken;
import com.igniscore.api.model.User;
import com.igniscore.api.model.VerificationToken;
import com.igniscore.api.repository.PasswordResetTokenRepository;
import com.igniscore.api.repository.UserRepository;
import com.igniscore.api.repository.VerificationTokenRepository;
import com.igniscore.api.service.EmailService;
import com.igniscore.api.service.JwtService;
import com.igniscore.api.service.TokenGeneratorService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("auth")
@SuppressWarnings("unused")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final TokenGeneratorService tokenGenerator;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository repository,
                          JwtService jwtService,
                          EmailService emailService,
                          PasswordResetTokenRepository tokenRepository,
                          VerificationTokenRepository verificationTokenRepository,
                          TokenGeneratorService tokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid @NonNull AutDTO data) {
        User user = this.repository.findByEmail(data.email());
        if (user != null && !user.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Email not verified. Please verify your account."));
        }

        var usernamepassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamepassword);

        var principal = auth.getPrincipal();
        if (!(principal instanceof User authenticatedUser)) {
            throw new RuntimeException("User authentication failed");
        }

        var token = jwtService.generateJwt(authenticatedUser);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        if (this.repository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        User newUser = new User();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(data.role());
        newUser.setActive(false);
        newUser.setEmailVerified(false);

        User savedUser = this.repository.save(newUser);

        String code = tokenGenerator.generateVerificationCode();
        VerificationToken verificationToken = new VerificationToken(
                code,
                savedUser,
                LocalDateTime.now().plusMinutes(15)
        );
        verificationTokenRepository.save(verificationToken);

        emailService.sendVerificationCode(savedUser.getEmail(), code);

        return ResponseEntity.ok(Map.of("message", "Registration successful. Verification code sent to email."));
    }

    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<Map<String, String>> requestPasswordRecovery(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            User user = this.repository.findByEmail(email);

            if (user != null) {
                tokenRepository.deleteByUser(user);

                String tokenGerado = UUID.randomUUID().toString();
                PasswordResetToken resetToken = new PasswordResetToken(tokenGerado, user, 15);
                tokenRepository.save(resetToken);

                emailService.sendRecoveryLink(user.getEmail(), tokenGerado);
            }

            return ResponseEntity.ok(Map.of("message", "Se o e-mail existir em nossa base, um link de recuperação será enviado."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Se o e-mail existir em nossa base, um link de recuperação será enviado."));
        }
    }

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            String newPassword = body.get("newPassword");

            PasswordResetToken resetToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Token inválido ou inexistente."));

            if (resetToken.isExpired()) {
                tokenRepository.delete(resetToken);
                return ResponseEntity.badRequest().body(Map.of("error", "Erro: Este link de recuperação expirou após 15 minutos."));
            }

            User user = resetToken.getUser();
            String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);
            user.setPassword(encryptedPassword);
            this.repository.save(user);

            tokenRepository.delete(resetToken);

            return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erro ao redefinir a senha: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-email")
    @Transactional
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid VerifyCodeDTO data) {
        VerificationToken verificationToken = verificationTokenRepository.findByCode(data.code())
                .orElse(null);

        if (verificationToken == null || !verificationToken.getUser().getEmail().equals(data.email())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid verification code."));
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            return ResponseEntity.badRequest().body(Map.of("error", "Verification code has expired."));
        }

        User user = verificationToken.getUser();
        user.setActive(true);
        user.setEmailVerified(true);
        repository.save(user);

        verificationTokenRepository.delete(verificationToken);

        return ResponseEntity.ok(Map.of("message", "Account verified successfully. You can now log in."));
    }
}
