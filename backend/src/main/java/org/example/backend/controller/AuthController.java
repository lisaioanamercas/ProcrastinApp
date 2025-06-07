package org.example.backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.security.jwt.JwtUtils;
import org.example.backend.security.user.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.JwtResponse;


@Tag(name = "Authentication", description = "User authentication and registration endpoints")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:5500", "http://localhost:5500"})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder encoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @Operation(summary = "User login", description = "Authenticates the user and returns a JWT token.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "400", description = "Invalid username or password")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Try to find user by email first, then by username
        String identifier = loginRequest.getUsername();
        User user = userRepository.findByEmail(identifier)
                .orElse(userRepository.findByUsername(identifier).orElse(null));
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getEmail()));
    }
// ...existing code...

    @Operation(summary = "User registration", description = "Registers a new user with the provided details.")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Username or email already exists")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername()))
            return ResponseEntity.badRequest().body("Username already exists");
        if (userRepository.existsByEmail(signUpRequest.getEmail()))
            return ResponseEntity.badRequest().body("Email already exists");

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}
//explica fiecare metoda in parte:
// 1. authenticateUser: Această metodă primește un obiect LoginRequest care conține numele de utilizator și parola.
//    Ea folosește AuthenticationManager pentru a autentifica utilizatorul. Dacă autentificarea este reușită, generează un token JWT și returnează un obiect JwtResponse care conține token-ul, numele de utilizator și email-ul utilizatorului.
// 2. registerUser: Această metodă primește un obiect RegisterRequest care conține informațiile necesare pentru înregistrarea unui nou utilizator.
//    Verifică dacă numele de utilizator sau email-ul există deja în baza de date. Dacă nu există, creează un nou obiect User, îl salvează în baza de date și returnează un mesaj de succes.
//    Dacă numele de utilizator sau email-ul există deja, returnează un mesaj de eroare corespunzător.
// Aceste metode sunt esențiale pentru gestionarea autentificării și înregistrării utilizatorilor în aplicație, asigurând atât securitatea, cât și funcționalitatea necesară pentru accesul la resursele protejate.
// Acest controller este responsabil pentru gestionarea autentificării și înregistrării utilizatorilor.
// El definește două endpoint-uri: unul pentru autentificare (login) și altul pentru înregistrare (register).
// Endpoint-urile sunt accesibile prin URL-ul "/api/auth" și folosesc metodele POST pentru a primi datele necesare.
