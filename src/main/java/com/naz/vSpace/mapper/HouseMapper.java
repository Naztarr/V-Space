package com.naz.vSpace.mapper;

import com.naz.vSpace.dto.HouseDto;
import com.naz.vSpace.entity.House;
import com.naz.vSpace.payload.HouseData;

public class HouseMapper {
    public static House mapToHouse(HouseDto dto, House house){
        house.setName(dto.name());
        house.setDescription(dto.description());
        house.setAddress(dto.address());
        house.setRoomCount(dto.roomCount());
        house.setType(dto.type());
        house.setAnnualCost(dto.annualCost());
        house.setUpfrontCost(dto.upfrontCost());
        house.setUpfrontCostDescription(dto.upfrontCostDescription());
        house.setYearCommitment(dto.yearCommitment());

        return house;
    }

    public static HouseData mapToHouseData(House house, HouseData data){
        data.setId(house.getId());
        data.setName(house.getName());
        data.setDescription(house.getDescription());
        data.setAddress(house.getAddress());
        data.setRoomCount(house.getRoomCount());
        data.setType(house.getType());
        data.setPhotoUrls(house.getPhotoUrls());
        data.setAnnualCost(house.getAnnualCost());
        data.setUpfrontCost(house.getUpfrontCost());
        data.setUpfrontCostDescription(house.getUpfrontCostDescription());
        data.setTotalCost(house.getTotalCost());
        data.setYearCommitment(house.getYearCommitment());

        return data;
    }

}