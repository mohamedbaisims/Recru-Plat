package com.example.backend.auth;

import com.example.backend.config.JwtService;
import com.example.backend.email.EmailService;
import com.example.backend.token.Token;
import com.example.backend.token.TokenRepository;
import com.example.backend.token.TokenType;
import com.example.backend.user.Role;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Builder
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();

        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void revokeAllUserTokens (User user){
        var validUserTokens=tokenRepository.findAllValidTokensByUser(user.getId());
        if(validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t ->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    private void saveUserToken(User user, String jwtToken) {
        var token=Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        // 👉 Si c’est un ADMIN ou MANAGER, pas besoin de MFA
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER) {
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .mfaEnabled(false)
                    .build();
        }

        // Sinon, générer et envoyer le code OTP (MFA)
        String code = generateSixDigitCode();
        user.setSecret(code);
        repository.save(user);
        emailService.sendAuthMfaCode(user.getEmail(), code);

        return AuthenticationResponse.builder()
                .accessToken("")
                .refreshToken("")
                .mfaEnabled(true)
                .build();
    }

    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        final String authHead= request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if(authHead==null ||!authHead.startsWith("Bearer")){

            return;
        }
        refreshToken=authHead.substring(7);
        userEmail= jwtService.extractUsername(refreshToken);
        if (userEmail != null ){
            var user =this.repository.findByEmail(userEmail).orElseThrow();

            if (jwtService.isTokenVaild(refreshToken,user)){
             var accessToken=jwtService.generateToken(user);
             revokeAllUserTokens(user);
             saveUserToken(user,accessToken);
             var authResponse=AuthenticationResponse.builder()
                     .accessToken(accessToken)
                     .refreshToken(refreshToken)
                     .build();
             new ObjectMapper().writeValue(response.getOutputStream(),authResponse);
            }
        }

    }

    public AuthenticationResponse verifyCode(VerificationRequest verificationRequest) {
        User user = repository.findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("No user found with %s", verificationRequest.getEmail())
                ));

        if (!verificationRequest.getCode().equals(user.getSecret())) {
            throw new BadCredentialsException("Code MFA incorrect");
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        user.setSecret(null); // ✅ Nettoyage du code
        repository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateSixDigitCode() {
        int code = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
