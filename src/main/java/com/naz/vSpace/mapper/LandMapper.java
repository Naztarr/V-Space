package com.naz.vSpace.mapper;

import com.naz.vSpace.dto.LandDto;
import com.naz.vSpace.entity.Land;
import com.naz.vSpace.payload.LandData;

public class LandMapper {
    public static Land mapToLand(LandDto dto, Land land){
        land.setName(dto.name());
        land.setDescription(dto.description());
        land.setLocation(dto.location());
        land.setAnnualCost(dto.annualCost());
        land.setUpfrontCost(dto.upfrontCost());
        land.setUpfrontCostDescription(dto.upfrontCostDescription());
        land.setYearCommitment(dto.yearCommitment());

        return land;
    }

    public static LandData mapToLandData(Land land, LandData data){
        data.setId(land.getId());
        data.setName(land.getName());
        data.setDescription(land.getDescription());
        data.setLocation(land.getLocation());
        data.setPhotoUrls(land.getPhotoUrls());
        data.setAnnualCost(land.getAnnualCost());
        data.setUpfrontCost(land.getUpfrontCost());
        data.setUpfrontCostDescription(land.getUpfrontCostDescription());
        data.setTotalCost(land.getTotalCost());
        data.setYearCommitment(land.getYearCommitment());

        return data;
    }
}
