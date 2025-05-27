package com.DriveAuto.user_service.Response;


public record LoginResponse(
            String token,
            Long userId,
            String username,
            String email,
            String role
 ) {}


