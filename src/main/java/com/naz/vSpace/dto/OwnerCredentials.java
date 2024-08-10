package com.naz.vSpace.dto;

import com.naz.vSpace.enums.IdType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

public record OwnerCredentials(
        @NotNull(message = "ID type is required")
        IdType type,
        @NotBlank(message = "ID number is required")
        String idNumber,
        @NotNull(message = "ID file is required")
        MultipartFile idFile,
        @NotBlank(message = "BVN is required")
        @Pattern(regexp = "\\d{11}", message = "BVN must be an 11 digit number")
        String bvn
) {
}
