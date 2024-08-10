package com.naz.vSpace.service;

import com.naz.vSpace.dto.HouseDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.HouseData;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface HouseService {

    ResponseEntity<ApiResponse<String>> postHouse(HouseDto dto);
    ResponseEntity<ApiResponse<String>> rentHouse(UUID houseId);
    ResponseEntity<ApiResponse<String>> cancelHouseRent(UUID rentId);
    ResponseEntity<ApiResponse<String>> approveHouseRent(UUID rentId);
    ResponseEntity<ApiResponse<List<HouseData>>> getAllHouses(Integer pageNumber, Integer pageSize);
    ResponseEntity<ApiResponse<List<HouseData>>> searchHouse(String keyword);
    ResponseEntity<ApiResponse<List<HouseData>>> viewAllOwnerHouses(Integer pageNumber, Integer pageSize);
    ResponseEntity<ApiResponse<HouseData>> viewHouseDetail(UUID id);
    ResponseEntity<ApiResponse<String>> updateHouse(UUID id, HouseDto dto);
    ResponseEntity<ApiResponse<String>> removeHouse(UUID id);
}
