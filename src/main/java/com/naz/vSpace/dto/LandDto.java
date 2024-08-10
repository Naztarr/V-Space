package com.naz.vSpace.dto;


import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;


public record LandDto(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,
        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,
        @NotBlank(message = "Location is required")
        @Size(max = 200, message = "Location must not exceed 200 characters")
        String location,
        @NotNull(message = "Photos are required")
        @NotEmpty(message = "At least one photo is required")
        MultipartFile[] photos,
        @NotNull(message = "Annual cost is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Annual cost must be greater than 0")
        Double annualCost,
        @NotNull(message = "Upfront cost is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Upfront cost must be greater than 0")
        Double upfrontCost,
        @NotBlank(message = "Upfront cost description is required")
        @Size(max = 300, message = "Upfront cost description must not exceed 300 characters")
        String upfrontCostDescription,
        @NotNull(message = "Year commitment is required")
        @Min(value = 1, message = "Year commitment must be at least 1 year")
        Integer yearCommitment
) {
}