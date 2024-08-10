package com.naz.vSpace.service;

import com.naz.vSpace.dto.OwnerCredentials;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.OwnerCredentialData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    ResponseEntity<ApiResponse<OwnerCredentialData>> verifyAsOwner(OwnerCredentials details);
    ResponseEntity<ApiResponse<String>> updateProfilePicture(MultipartFile photo) throws IOException;
}
