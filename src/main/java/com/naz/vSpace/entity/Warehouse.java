package com.naz.vSpace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "ware_houses")
@Getter
@Setter
public class Warehouse extends BaseEntity{
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "address")
    private String address;
    @ElementCollection
    @CollectionTable(name = "warehouse_photos", joinColumns = @JoinColumn(name = "warehouse_id"))
    @Column(name = "photos")
    private List<String> photoUrls;
    @Column(name = "annual_cost")
    private Double annualCost;
    @Column(name = "upfront_cost")
    private Double upfrontCost;
    @Column(name = "cost_description")
    private String upfrontCostDescription;
    @Column(name = "is_available")
    private Boolean isAvailable;
    @Column(name = "total_cost")
    private Double totalCost;
    @Column(name = "year_commitment")
    private Integer yearCommitment;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
