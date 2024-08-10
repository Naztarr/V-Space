package com.naz.vSpace.controller;

import com.naz.vSpace.dto.HouseDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.HouseData;
import com.naz.vSpace.service.HouseService;
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
        name = "Houses",
        description = "REST APIs allowing all house related operations"
)
public class HouseController {
    private final HouseService houseService;

    @PostMapping("/houses")
    @Operation(summary = "Post house",
            description = "Allows an owner to post his house for rent")
    public ResponseEntity<ApiResponse<String>> postHouse(@RequestBody HouseDto dto){
        return houseService.postHouse(dto);
    }

    @PostMapping("/houses/{houseId}/rent")
    @Operation(summary = "Rent house",
            description = "Allows a customer to initiate rent for a house")
    public ResponseEntity<ApiResponse<String>> rentHouse(@PathVariable UUID houseId){
        return houseService.rentHouse(houseId);
    }

    @DeleteMapping("/houses/{rentId}")
    @Operation(summary = "Cancel house rent",
            description = "A user can cancel a rent initiated if no longer interested")
    public ResponseEntity<ApiResponse<String>> cancelHouseRent(@PathVariable UUID rentId){
        return houseService.cancelHouseRent(rentId);
    }

    @PutMapping("/houses/{rentId}/approve")
    @Operation(summary = "Approve house rent",
            description = "Allows an owner to approve a rent initiated for his house after agreement with the customer")
    public ResponseEntity<ApiResponse<String>> approveHouseRent(@PathVariable UUID rentId){
        return houseService.approveHouseRent(rentId);
    }

    @GetMapping("/houses")
    @Operation(summary = "House listing",
            description = "Displays a paginated list of all available houses from the most recent")
    public ResponseEntity<ApiResponse<List<HouseData>>> getAllHouses(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize){
        return houseService.getAllHouses(pageNumber, pageSize);
    }

    @GetMapping("/houses/search")
    @Operation(summary = "House search",
            description = "Searches for houses by a criterion through the keywords supplied")
    public ResponseEntity<ApiResponse<List<HouseData>>> searchHouse(@RequestParam String keyword){
        return houseService.searchHouse(keyword);
    }

    @GetMapping("/users/houses")
    @Operation(summary = "Owner houses",
            description = "This allows an owner to view all houses posted by him")
    public ResponseEntity<ApiResponse<List<HouseData>>> viewAllOwnerHouses(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                           @RequestParam(defaultValue = "10")Integer pageSize){
        return houseService.viewAllOwnerHouses(pageNumber, pageSize);
    }

    @GetMapping("/houses/{houseId}")
    @Operation(summary = "View house detail",
            description = "Displays full information of a house")
    public ResponseEntity<ApiResponse<HouseData>> viewHouseDetail(@PathVariable UUID houseId){
        return houseService.viewHouseDetail(houseId);
    }

    @PutMapping("/houses/{houseId}")
    @Operation(summary = "Update house",
            description = "Allows an owner to update the details of a posted house")
    public ResponseEntity<ApiResponse<String>> updateHouse(@PathVariable UUID houseId,
                                                           @RequestBody HouseDto dto){
        return houseService.updateHouse(houseId, dto);
    }

    @PreAuthorize("hasRole('LESSOR') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @DeleteMapping("/houses/{houseId}")
    @Operation(summary = "Remove house",
            description = "This endpoint allows only the owner or an admin to remove a house posted on the platform")
    public ResponseEntity<ApiResponse<String>> removeHouse(@PathVariable UUID houseId){
        return houseService.removeHouse(houseId);
    }
}
