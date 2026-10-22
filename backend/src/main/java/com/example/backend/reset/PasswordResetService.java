package com.example.backend.reset;

import com.example.backend.email.EmailService;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void sendVerificationCode(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email introuvable"));

        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        user.setSecrets(code);
        userRepository.save(user);

        emailService.sendMfaCode(user.getEmail(), "Votre code de réinitialisation : " + code);
    }

    public void resetPassword(ResetPasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email introuvable"));
        String userSecret = user.getSecrets();
        if (userSecret == null || !request.getCode().trim().equals(userSecret.trim())) {
            throw new BadCredentialsException("Code incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setSecrets(null);
        userRepository.save(user);
    }
}