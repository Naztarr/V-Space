package com.naz.vSpace.service;

import com.naz.vSpace.dto.LandDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.LandData;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface LandService {

    ResponseEntity<ApiResponse<String>> postLand(LandDto dto);
    ResponseEntity<ApiResponse<String>> rentLand(UUID landId);
    ResponseEntity<ApiResponse<String>> cancelLandRent(UUID rentId);
    ResponseEntity<ApiResponse<String>> approveLandRent(UUID rentId);
    ResponseEntity<ApiResponse<List<LandData>>> getAllLands(Integer pageNumber, Integer pageSize);
    ResponseEntity<ApiResponse<List<LandData>>> searchLand(String keyword);
    ResponseEntity<ApiResponse<List<LandData>>> viewAllOwnerLands(Integer pageNumber, Integer pageSize);
    ResponseEntity<ApiResponse<LandData>> viewLandDetail(UUID id);
    ResponseEntity<ApiResponse<String>> updateLand(UUID id, LandDto dto);
    ResponseEntity<ApiResponse<String>> removeLand(UUID id);
}
