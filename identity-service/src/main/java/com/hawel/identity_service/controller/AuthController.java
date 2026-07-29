package com.hawel.identity_service.controller;

import com.hawel.identity_service.dto.request.RefreshTokenRequest;
import com.hawel.identity_service.dto.request.SendOtpRequest;
import com.hawel.identity_service.dto.request.VerifyOtpRequest;
import com.hawel.identity_service.dto.response.IdentityAuthResponse;
import com.hawel.identity_service.dto.response.AuthResponse;
import com.hawel.identity_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
@Tag(
        name = "Authentication",
        description = "Operations related to authentication"
)
@Slf4j
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    @Operation(
            summary = "Send OTP",
            description = "Generate and send a one-time password (OTP) to the customer's registered phone number."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid phone number"),
            @ApiResponse(responseCode = "429", description = "Too many OTP requests")
    })
    public ResponseEntity<IdentityAuthResponse<Void>> sendOtp(@Valid @RequestBody SendOtpRequest request) {

        log.info("Received OTP request for phoneNumber={}", request.getPhoneNumber());

        authService.sendOtp(request);

        log.info("OTP sent successfully to phoneNumber={}", request.getPhoneNumber());

        return ResponseEntity.ok(IdentityAuthResponse.success("OTP sent successfully."));
    }

    @PostMapping("/verify-otp")
    @Operation(
            summary = "Verify OTP",
            description = "Verify the OTP and authenticate the customer. If the customer does not exist, a new account will be created automatically."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
            @ApiResponse(responseCode = "401", description = "OTP verification failed")
    })
    public ResponseEntity<IdentityAuthResponse<AuthResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {

        log.info("Verifying OTP for phoneNumber={}", request.getPhoneNumber());

        AuthResponse response = authService.verifyOtp(request);

        log.info("Customer authenticated successfully. userId={}", response.getUserId());

       // return ResponseEntity.ok(IdentityAuthResponse.success("Authentication successful.", response));
        return ResponseEntity.ok(
                IdentityAuthResponse.success(
                        "Authentication successful.",
                        response
                )
        );
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh Access Token",
            description = "Generate a new access token using a valid refresh token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Refresh token is invalid or expired")
    })
    public ResponseEntity<IdentityAuthResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        log.info("Refreshing access token.");

        AuthResponse response = authService.refreshToken(request);

        log.info("Access token refreshed successfully.");

        return ResponseEntity.ok(IdentityAuthResponse.success("Access token refreshed successfully.", response));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout",
            description = "Logout the authenticated user and revoke the refresh token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<IdentityAuthResponse<Void>> logout(@RequestHeader("Authorization") String authorizationHeader) {

        log.info("Logout request received.");

        authService.logout(authorizationHeader);

        log.info("User logged out successfully.");

        return ResponseEntity.ok(IdentityAuthResponse.success("Logged out successfully."));
    }

}
