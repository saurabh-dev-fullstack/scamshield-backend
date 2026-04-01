package com.scamshield.backend.controller;

import com.scamshield.backend.dto.AuthResponse;
import com.scamshield.backend.dto.LoginRequest;
import com.scamshield.backend.dto.RegisterRequest;
import com.scamshield.backend.entity.User;
import com.scamshield.backend.repository.UserRepository;
import com.scamshield.backend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // 🔐 Secret key for admin registration (move to env later)
    private static final String ADMIN_SECRET = "SCAMSHIELD_ADMIN_SECRET_2025";

    public AuthController(
            UserRepository userRepo,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // ======================
    // REGISTER (ADMIN / USER)
    // ======================
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(
            @RequestBody RegisterRequest request,
            @RequestHeader("X-ADMIN-SECRET") String secret
    ) {
        if (!secret.equals("SCAMSHIELD_ADMIN_SECRET_2025")) {
            return ResponseEntity.status(403).body("Invalid admin secret");
        }

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Admin already exists");
        }

        User admin = new User();
        admin.setEmail(request.getEmail());
        admin.setPassword(request.getPassword()); // later hash
        admin.setRole("ADMIN");

        userRepo.save(admin);

        String token = jwtService.generateToken(admin);
        return ResponseEntity.ok(new AuthResponse(token));
    }


    // ==========
    // LOGIN
    // ==========
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token));
    }

}
