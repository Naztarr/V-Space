package com.naz.vSpace.controller;

import com.naz.vSpace.dto.LandDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.LandData;
import com.naz.vSpace.service.LandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(
        name = "Lands",
        description = "REST APIs allowing all land related operations"
)
public class LandController {
    private final LandService landService;

    @PostMapping("/lands")
    @Operation(summary = "Post land",
            description = "Allows an owner to post his land for rent")
    public ResponseEntity<ApiResponse<String>> postLand(@RequestBody LandDto dto){
        return landService.postLand(dto);
    }

    @PostMapping("/lands/{landId}/rent")
    @Operation(summary = "Rent land",
            description = "Allows a customer to initiate rent for a land")
    public ResponseEntity<ApiResponse<String>> rentLand(@PathVariable UUID landId){
        return landService.rentLand(landId);
    }

    @DeleteMapping("/lands/{rentId}")
    @Operation(summary = "Cancel land rent",
            description = "A user can cancel a rent initiated if no longer interested")
    public ResponseEntity<ApiResponse<String>> cancelLandRent(@PathVariable UUID rentId){
        return landService.cancelLandRent(rentId);
    }

    @PutMapping("/lands/{rentId}/approve")
    @Operation(summary = "Approve land rent",
            description = "Allows an owner to approve a rent initiated for his land after agreement with the customer")
    public ResponseEntity<ApiResponse<String>> approveLandRent(@PathVariable UUID rentId){
        return landService.approveLandRent(rentId);
    }

    @GetMapping("/lands")
    @Operation(summary = "House listing",
            description = "Displays a paginated list of all available lands from the most recent")
    public ResponseEntity<ApiResponse<List<LandData>>> getAllLands(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                   @RequestParam(defaultValue = "10")Integer pageSize){
        return landService.getAllLands(pageNumber, pageSize);
    }

    @GetMapping("/lands/search")
    @Operation(summary = "House search",
            description = "Searches for lands by a criterion through the keywords supplied")
    public ResponseEntity<ApiResponse<List<LandData>>> searchLand(@RequestParam String keyword){
        return landService.searchLand(keyword);
    }

    @GetMapping("/users/lands")
    @Operation(summary = "Owner lands",
            description = "This allows an owner to view all lands posted by him")
    public ResponseEntity<ApiResponse<List<LandData>>> viewAllOwnerLands(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize){
        return landService.viewAllOwnerLands(pageNumber, pageSize);
    }

    @GetMapping("/lands/{landId}")
    @Operation(summary = "View land detail",
            description = "Displays full information of a land")
    public ResponseEntity<ApiResponse<LandData>> viewLandDetail(@PathVariable UUID landId){
        return landService.viewLandDetail(landId);
    }

    @PutMapping("/lands/{landId}")
    @Operation(summary = "Update land",
            description = "Allows an owner to update the details of a posted land")
    public ResponseEntity<ApiResponse<String>> updateLand(@PathVariable UUID landId, @RequestBody LandDto dto){
        return landService.updateLand(landId, dto);
    }

    @PreAuthorize("hasRole('LESSOR') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @DeleteMapping("/lands/{landId}")
    @Operation(summary = "Remove land",
            description = "This endpoint allows only the owner or an admin to remove a land posted on the platform")
    public ResponseEntity<ApiResponse<String>> removeLand(@PathVariable UUID landId){
        return landService.removeLand(landId);
    }
}