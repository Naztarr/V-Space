package com.naz.vSpace.controller;

import com.naz.vSpace.dto.HouseDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.HouseData;
import com.naz.vSpace.service.HouseService;
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
        name = "Houses",
        description = "REST APIs allowing all house related operations"
)
public class HouseController {
    private final HouseService houseService;

    @PostMapping("/houses")
    public ResponseEntity<ApiResponse<String>> postHouse(@RequestBody HouseDto dto){
        return houseService.postHouse(dto);
    }

    @PostMapping("/houses/{houseId}/rent")
    public ResponseEntity<ApiResponse<String>> rentHouse(@PathVariable UUID houseId){
        return houseService.rentHouse(houseId);
    }

    @DeleteMapping("/houses/{rentId}")
    public ResponseEntity<ApiResponse<String>> cancelHouseRent(@PathVariable UUID rentId){
        return houseService.cancelHouseRent(rentId);
    }

    @PutMapping("/houses/{rentId}/approve")
    public ResponseEntity<ApiResponse<String>> approveHouseRent(@PathVariable UUID rentId){
        return houseService.approveHouseRent(rentId);
    }

    @GetMapping("/houses")
    public ResponseEntity<ApiResponse<List<HouseData>>> getAllHouses(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize){
        return houseService.getAllHouses(pageNumber, pageSize);
    }

    @GetMapping("/houses/search")
    public ResponseEntity<ApiResponse<List<HouseData>>> searchHouse(@RequestParam String keyword){
        return houseService.searchHouse(keyword);
    }

    @GetMapping("/users/houses")
    public ResponseEntity<ApiResponse<List<HouseData>>> viewAllOwnerHouses(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                           @RequestParam(defaultValue = "10")Integer pageSize){
        return houseService.viewAllOwnerHouses(pageNumber, pageSize);
    }

    @GetMapping("/houses/{houseId}")
    public ResponseEntity<ApiResponse<HouseData>> viewHouseDetail(@PathVariable UUID houseId){
        return houseService.viewHouseDetail(houseId);
    }

    @PutMapping("/houses/{houseId}")
    public ResponseEntity<ApiResponse<String>> updateHouse(@PathVariable UUID houseId,
                                                           @RequestBody HouseDto dto){
        return houseService.updateHouse(houseId, dto);
    }

    @PreAuthorize("hasRole('LESSOR') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @DeleteMapping("/houses/{houseId}")
    public ResponseEntity<ApiResponse<String>> removeHouse(@PathVariable UUID houseId){
        return houseService.removeHouse(houseId);
    }
}
