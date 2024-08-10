package com.naz.vSpace.service;

import com.naz.vSpace.dto.LoginDto;
import com.naz.vSpace.dto.ResetPasswordDto;
import com.naz.vSpace.dto.SignupDto;
import com.naz.vSpace.enums.VerifyType;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.UserResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    ResponseEntity<ApiResponse<String>> signup(SignupDto signupDto);
    ResponseEntity<ApiResponse<String>> confirmEmail(String token);
    ResponseEntity<ApiResponse<UserResponse>> login(LoginDto loginDto);
    ResponseEntity<ApiResponse<String>> sendLink(String email, VerifyType type);
    ResponseEntity<ApiResponse<String>> resetPassword(String token, ResetPasswordDto dto);
}
