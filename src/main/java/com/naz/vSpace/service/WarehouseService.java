package com.naz.vSpace.service;

import com.naz.vSpace.dto.WarehouseDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.WarehouseData;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface WarehouseService {

    ResponseEntity<ApiResponse<String>> postWarehouse(WarehouseDto dto);
    ResponseEntity<ApiResponse<String>> rentWarehouse(UUID warehouseId);
    ResponseEntity<ApiResponse<String>> cancelWarehouseRent(UUID rentId);
    ResponseEntity<ApiResponse<String>> approveWarehouseRent(UUID rentId);
    ResponseEntity<ApiResponse<List<WarehouseData>>> getAllWarehouses(Integer pageNumber, Integer pageSize);
    ResponseEntity<ApiResponse<List<WarehouseData>>> searchWarehouse(String keyword);
    ResponseEntity<ApiResponse<List<WarehouseData>>> viewAllOwnerWarehouses(Integer pageNumber, Integer pageSize);
    ResponseEntity<ApiResponse<WarehouseData>> viewWarehouseDetail(UUID id);
    ResponseEntity<ApiResponse<String>> updateWarehouse(UUID id, WarehouseDto dto);
    ResponseEntity<ApiResponse<String>> removeWarehouse(UUID id);
}
