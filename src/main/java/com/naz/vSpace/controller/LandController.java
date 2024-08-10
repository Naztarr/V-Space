package com.naz.vSpace.controller;

import com.naz.vSpace.dto.LandDto;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.LandData;
import com.naz.vSpace.service.LandService;
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
    public ResponseEntity<ApiResponse<String>> postLand(@RequestBody LandDto dto){
        return landService.postLand(dto);
    }

    @PostMapping("/lands/{landId}/rent")
    public ResponseEntity<ApiResponse<String>> rentLand(@PathVariable UUID landId){
        return landService.rentLand(landId);
    }

    @DeleteMapping("/lands/{rentId}")
    public ResponseEntity<ApiResponse<String>> cancelLandRent(@PathVariable UUID rentId){
        return landService.cancelLandRent(rentId);
    }

    @PutMapping("/lands/{rentId}/approve")
    public ResponseEntity<ApiResponse<String>> approveLandRent(@PathVariable UUID rentId){
        return landService.approveLandRent(rentId);
    }

    @GetMapping("/lands")
    public ResponseEntity<ApiResponse<List<LandData>>> getAllLands(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                   @RequestParam(defaultValue = "10")Integer pageSize){
        return landService.getAllLands(pageNumber, pageSize);
    }

    @GetMapping("/lands/search")
    public ResponseEntity<ApiResponse<List<LandData>>> searchLand(@RequestParam String keyword){
        return landService.searchLand(keyword);
    }

    @GetMapping("/users/lands")
    public ResponseEntity<ApiResponse<List<LandData>>> viewAllOwnerLands(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize){
        return landService.viewAllOwnerLands(pageNumber, pageSize);
    }

    @GetMapping("/lands/{landId}")
    public ResponseEntity<ApiResponse<LandData>> viewLandDetail(@PathVariable UUID landId){
        return landService.viewLandDetail(landId);
    }

    @PutMapping("/lands/{landId}")
    public ResponseEntity<ApiResponse<String>> updateLand(@PathVariable UUID landId, @RequestBody LandDto dto){
        return landService.updateLand(landId, dto);
    }

    @PreAuthorize("hasRole('LESSOR') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @DeleteMapping("/lands/{landId}")
    public ResponseEntity<ApiResponse<String>> removeLand(@PathVariable UUID landId){
        return landService.removeLand(landId);
    }
}
