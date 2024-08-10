package com.naz.vSpace.payload;

import com.naz.vSpace.entity.User;
import com.naz.vSpace.enums.Type;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
public class HouseData {
    private UUID id;

    private String name;

    private String description;

    private String address;

    private Integer roomCount;

    private Type type;

    private List<String> photoUrls;

    private Double annualCost;

    private Double upfrontCost;

    private String upfrontCostDescription;

    private Double totalCost;

    private Integer yearCommitment;
}
