package com.naz.vSpace.controller;

import com.naz.vSpace.dto.WarehouseDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.WarehouseData;
import com.naz.vSpace.service.WarehouseService;
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
        name = "Warehouses",
        description = "REST APIs allowing all warehouse related operations"
)
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping("/warehouses")
    @Operation(summary = "Post warehouse",
            description = "Allows an owner to post his warehouse for rent")
    public ResponseEntity<ApiResponse<String>> postWarehouse(@RequestBody WarehouseDto dto){
        return warehouseService.postWarehouse(dto);
    }

    @PostMapping("/warehouses/{warehouseId}/rent")
    @Operation(summary = "Rent warehouse",
            description = "Allows a customer to initiate rent for a warehouse")
    public ResponseEntity<ApiResponse<String>> rentWarehouse(@PathVariable UUID warehouseId){
        return warehouseService.rentWarehouse(warehouseId);
    }

    @DeleteMapping("/warehouses/{rentId}")
    @Operation(summary = "Cancel warehouse rent",
            description = "A user can cancel a rent initiated if no longer interested")
    public ResponseEntity<ApiResponse<String>> cancelWarehouseRent(@PathVariable UUID rentId){
        return warehouseService.cancelWarehouseRent(rentId);
    }

    @PutMapping("/warehouses/{rentId}/approve")
    @Operation(summary = "Approve warehouse rent",
            description = "Allows an owner to approve a rent initiated for his warehouse after agreement with the customer")
    public ResponseEntity<ApiResponse<String>> approveWarehouseRent(@PathVariable UUID rentId){
        return warehouseService.approveWarehouseRent(rentId);
    }

    @GetMapping("/warehouses")
    @Operation(summary = "House listing",
            description = "Displays a paginated list of all available warehouses from the most recent")
    public ResponseEntity<ApiResponse<List<WarehouseData>>> getAllWarehouses(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                             @RequestParam(defaultValue = "10") Integer pageSize){
        return warehouseService.getAllWarehouses(pageNumber, pageSize);
    }

    @GetMapping("/warehouses/search")
    @Operation(summary = "House search",
            description = "Searches for warehouses by a criterion through the keywords supplied")
    public ResponseEntity<ApiResponse<List<WarehouseData>>> searchWarehouse(@RequestParam String keyword){
        return warehouseService.searchWarehouse(keyword);
    }

    @GetMapping("/users/warehouses")
    @Operation(summary = "Owner warehouses",
            description = "This allows an owner to view all warehouses posted by him")
    public ResponseEntity<ApiResponse<List<WarehouseData>>> viewAllOwnerWarehouses(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                                   @RequestParam(defaultValue = "10")Integer pageSize){
        return warehouseService.viewAllOwnerWarehouses(pageNumber, pageSize);
    }

    @GetMapping("/warehouses/{warehouseId}")
    @Operation(summary = "View warehouse detail",
            description = "Displays full information of a warehouse")
    public ResponseEntity<ApiResponse<WarehouseData>> viewWarehouseDetail(@PathVariable UUID warehouseId){
        return warehouseService.viewWarehouseDetail(warehouseId);
    }

    @PutMapping("/warehouses/{warehouseId}")
    @Operation(summary = "Update warehouse",
            description = "Allows an owner to update the details of a posted warehouse")
    public ResponseEntity<ApiResponse<String>> updateWarehouse(@PathVariable UUID warehouseId,
                                                               @RequestBody WarehouseDto dto){
        return warehouseService.updateWarehouse(warehouseId, dto);
    }

    @PreAuthorize("hasRole('LESSOR') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @DeleteMapping("/warehouses/{warehouseId}")
    @Operation(summary = "Remove warehouse",
            description = "This endpoint allows only the owner or an admin to remove a warehouse posted on the platform")
    public ResponseEntity<ApiResponse<String>> removeWarehouse(@PathVariable UUID warehouseId){
        return warehouseService.removeWarehouse(warehouseId);
    }
}

