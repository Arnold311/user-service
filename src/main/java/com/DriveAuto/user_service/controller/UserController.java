package com.DriveAuto.user_service.controller;

import com.DriveAuto.user_service.Dto.AuthentificatiobDto;
import com.DriveAuto.user_service.Dto.UserRegistrationDto;
import com.DriveAuto.user_service.Dto.UserUpdateDto;
import com.DriveAuto.user_service.Response.ApiResponse;
import com.DriveAuto.user_service.Response.LoginResponse;
import com.DriveAuto.user_service.Response.UserResponse;
import com.DriveAuto.user_service.model.User;
import com.DriveAuto.user_service.security.JwtUtil;
import com.DriveAuto.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Enregistrer un nouvel utilisateur")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto
    ) {
        User savedUser = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "User registered successfully",
                        new UserResponse(savedUser))
                );
    }

    @Operation(summary = "Connexion utilisateur")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(
            @Valid @RequestBody AuthentificatiobDto authenticationDto
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationDto.username(),
                        authenticationDto.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication.getName());

        User user = userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new ApiResponse<>(
                "Login successful",
                new LoginResponse(
                        jwt,
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name()
                )
        ));
    }

    @Operation(summary = "Récupérer l'utilisateur courant")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        User user = userService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(
                new ApiResponse<>("Current user retrieved", new UserResponse(user))
        );
    }

    @Operation(summary = "Mettre à jour les informations utilisateur")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userUpdateDto
    ) {
        return userService.updateUser(id, userUpdateDto)
                .map(user -> ResponseEntity.ok(
                        new ApiResponse<>("User updated", new UserResponse(user))
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("User not found", null)));
    }

    @Operation(summary = "Récupérer tous les utilisateurs")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>("Users retrieved successfully", users)
        );
    }

    @Operation(summary = "Récupérer un utilisateur par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(
                        new ApiResponse<>("User found", new UserResponse(user))
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("User not found", null)));
    }

    @Operation(summary = "Supprimer un utilisateur")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                new ApiResponse<>("User deleted successfully", null)
        );
    }


}