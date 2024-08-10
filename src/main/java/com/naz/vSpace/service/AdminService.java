package com.naz.vSpace.service;

import com.naz.vSpace.dto.AdminDto;
import com.naz.vSpace.dto.SignupDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.UserData;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AdminService {
    ResponseEntity<ApiResponse<String>> verifyOwner(UUID credentialId, String ownerEmail);
    ResponseEntity<ApiResponse<UserData>> createAdmin(AdminDto dto);
}
