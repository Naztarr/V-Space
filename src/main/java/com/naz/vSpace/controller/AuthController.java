package com.naz.vSpace.controller;

import com.naz.vSpace.dto.LoginDto;
import com.naz.vSpace.dto.ResetPasswordDto;
import com.naz.vSpace.dto.SignupDto;
import com.naz.vSpace.enums.VerifyType;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.UserResponse;
import com.naz.vSpace.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody SignupDto signupDto){
        return authenticationService.signup(signupDto);
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<ApiResponse<String>> confirmEmail(@RequestParam String token){
        return authenticationService.confirmEmail(token);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginDto loginDto){
        return authenticationService.login(loginDto);
    }

    @GetMapping("send-link")
    public ResponseEntity<ApiResponse<String>> sendLink(@RequestParam String email, @RequestParam VerifyType type){
        return authenticationService.sendLink(email, type);
    }

    @PostMapping("password-reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String token, @RequestBody ResetPasswordDto dto){
        return authenticationService.resetPassword(token, dto);
    }
}
