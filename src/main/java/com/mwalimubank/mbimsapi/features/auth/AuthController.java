package com.mwalimubank.mbimsapi.features.auth;

import com.mwalimubank.mbimsapi.features.auth.dtos.*;
import com.mwalimubank.mbimsapi.features.auth.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public  ResponseEntity<RegisterResponse>  register(@RequestBody RegisterRequest registerRequest) {
        RegisterResponse response =  authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    // --------- VERIFY OTP ---------
    @PostMapping("/verify-otp")
    public ResponseEntity<OtpVerificationResponse> verifyOtp(
            @RequestParam String email,
            @RequestBody OtpVerificationRequest request
    ) {
        boolean isValid = authService.verifyOtp(email, request.getOtp());
        OtpVerificationResponse response = new OtpVerificationResponse(isValid);
        return ResponseEntity.ok(response);
    }


    // --------- PASSWORD RECOVERY REQUEST ---------
    @PostMapping ("recover-password")
    public ResponseEntity<PasswordRecoveryResponse> passwordRecoveryRequest(
            @RequestBody RecoverPasswordRequest request
    ) {
        authService.passwordRecoveryRequest(request);
        PasswordRecoveryResponse response = new PasswordRecoveryResponse("OTP sent for password recovery");
        return ResponseEntity.ok(response);
    }

    // --------- CHANGE PASSWORD ---------
    @PostMapping("/change-password/{id}")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
      ChangePasswordResponse response =  authService.changePassword(id, request);
        return ResponseEntity.ok(response);


    }

}