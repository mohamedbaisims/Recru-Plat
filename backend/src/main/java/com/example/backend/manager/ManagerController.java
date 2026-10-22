package com.example.backend.manager;

import com.example.backend.user.Role;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.example.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> createManager(@RequestBody CreateManagerRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }

        User manager = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MANAGER)
                .build();

        userRepository.save(manager);
        return ResponseEntity.ok("Manager créé avec succès !");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllManagers() {
        List<User> managers = userService.getAllManagers();
        return ResponseEntity.ok(managers);
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateManagerPassword(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body
    ) {
        String newPassword = body.get("newPassword");
        userService.updateManagerPassword(id, newPassword);
        return ResponseEntity.ok("Mot de passe mis à jour avec succès");
    }
}
