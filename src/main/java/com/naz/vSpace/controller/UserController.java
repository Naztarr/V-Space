package com.naz.vSpace.controller;

import com.naz.vSpace.dto.OwnerCredentials;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.OwnerCredentialData;
import com.naz.vSpace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(
        name = "Users",
        description = "REST APIs allowing all user related operations"
)
public class UserController {
    private final UserService userService;
    @PostMapping("/users/verify")
    @Operation(
            summary = "Verify as owner",
            description = "Allows a user to verify as an owner by uploading the required credentials"
    )
    public ResponseEntity<ApiResponse<OwnerCredentialData>> verifyAsOwner(@RequestBody OwnerCredentials credentials){
        return userService.verifyAsOwner(credentials);
    }

    @PostMapping("/users/profile")
    @Operation(
            summary = "Picture upload",
            description = "Allows the user to update his profile picture"
    )
    public ResponseEntity<ApiResponse<String>> uploadProfilePicture(@RequestParam MultipartFile photo) throws IOException {
        return userService.updateProfilePicture(photo);
    }
}
