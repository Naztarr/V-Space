package com.naz.vSpace.mapper;

import com.naz.vSpace.dto.WarehouseDto;
import com.naz.vSpace.entity.Warehouse;
import com.naz.vSpace.payload.WarehouseData;

public class WarehouseMapper {
    public static Warehouse mapToWarehouse(WarehouseDto dto, Warehouse warehouse){
        warehouse.setName(dto.name());
        warehouse.setDescription(dto.description());
        warehouse.setAddress(dto.address());
        warehouse.setAnnualCost(dto.annualCost());
        warehouse.setUpfrontCost(dto.upfrontCost());
        warehouse.setUpfrontCostDescription(dto.upfrontCostDescription());
        warehouse.setYearCommitment(dto.yearCommitment());

        return warehouse;
    }

    public static WarehouseData mapToWarehouseData(Warehouse warehouse, WarehouseData data){
        data.setId(warehouse.getId());
        data.setName(warehouse.getName());
        data.setDescription(warehouse.getDescription());
        data.setAddress(warehouse.getAddress());
        data.setPhotoUrls(warehouse.getPhotoUrls());
        data.setAnnualCost(warehouse.getAnnualCost());
        data.setUpfrontCost(warehouse.getUpfrontCost());
        data.setUpfrontCostDescription(warehouse.getUpfrontCostDescription());
        data.setTotalCost(warehouse.getTotalCost());
        data.setYearCommitment(warehouse.getYearCommitment());

        return data;
    }
}
