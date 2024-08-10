package com.naz.vSpace.controller;

import com.naz.vSpace.dto.AdminDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.UserData;
import com.naz.vSpace.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(
        name = "Admins",
        description = "REST APIs allowing only admin specific operations"
)
public class AdminController {
    private final AdminService adminService;

    @PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
    @PostMapping("/admins/{credentialId}")
    @Operation(
            summary = "Verify owner",
            description = "Allows admins to verify an owner after reviewing credentials"
    )
    public ResponseEntity<ApiResponse<String>> verifyOwner(@PathVariable UUID credentialId, @RequestParam String email){
        return adminService.verifyOwner(credentialId, email);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/superadmins/new-admin")
    @Operation(
            summary = "Create admin",
            description = "Allows superadmin to add an admin"
    )
    public ResponseEntity<ApiResponse<UserData>> createAdmin(@RequestBody AdminDto dto){
        return adminService.createAdmin(dto);
    }
}
