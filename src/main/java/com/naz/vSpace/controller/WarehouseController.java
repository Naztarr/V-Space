package com.naz.vSpace.controller;

import com.naz.vSpace.dto.WarehouseDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.WarehouseData;
import com.naz.vSpace.service.WarehouseService;
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
    public ResponseEntity<ApiResponse<String>> postWarehouse(@RequestBody WarehouseDto dto){
        return warehouseService.postWarehouse(dto);
    }

    @PostMapping("/warehouses/{warehouseId}/rent")
    public ResponseEntity<ApiResponse<String>> rentWarehouse(@PathVariable UUID warehouseId){
        return warehouseService.rentWarehouse(warehouseId);
    }

    @DeleteMapping("/warehouses/{rentId}")
    public ResponseEntity<ApiResponse<String>> cancelWarehouseRent(@PathVariable UUID rentId){
        return warehouseService.cancelWarehouseRent(rentId);
    }

    @PutMapping("/warehouses/{rentId}/approve")
    public ResponseEntity<ApiResponse<String>> approveWarehouseRent(@PathVariable UUID rentId){
        return warehouseService.approveWarehouseRent(rentId);
    }

    @GetMapping("/warehouses")
    public ResponseEntity<ApiResponse<List<WarehouseData>>> getAllWarehouses(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                             @RequestParam(defaultValue = "10") Integer pageSize){
        return warehouseService.getAllWarehouses(pageNumber, pageSize);
    }

    @GetMapping("/warehouses/search")
    public ResponseEntity<ApiResponse<List<WarehouseData>>> searchWarehouse(@RequestParam String keyword){
        return warehouseService.searchWarehouse(keyword);
    }

    @GetMapping("/users/warehouses")
    public ResponseEntity<ApiResponse<List<WarehouseData>>> viewAllOwnerWarehouses(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                                   @RequestParam(defaultValue = "10")Integer pageSize){
        return warehouseService.viewAllOwnerWarehouses(pageNumber, pageSize);
    }

    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<ApiResponse<WarehouseData>> viewWarehouseDetail(@PathVariable UUID warehouseId){
        return warehouseService.viewWarehouseDetail(warehouseId);
    }

    @PutMapping("/warehouses/{warehouseId}")
    public ResponseEntity<ApiResponse<String>> updateWarehouse(@PathVariable UUID warehouseId,
                                                               @RequestBody WarehouseDto dto){
        return warehouseService.updateWarehouse(warehouseId, dto);
    }

    @PreAuthorize("hasRole('LESSOR') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @DeleteMapping("/warehouses/{warehouseId}")
    public ResponseEntity<ApiResponse<String>> removeWarehouse(@PathVariable UUID warehouseId){
        return warehouseService.removeWarehouse(warehouseId);
    }
}
