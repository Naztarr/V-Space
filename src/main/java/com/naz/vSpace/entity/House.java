package com.naz.vSpace.entity;

import com.naz.vSpace.enums.Type;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "houses")
@Getter
@Setter
public class House extends  BaseEntity{
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "address")
    private String address;
    @Column(name = "room_count")
    private Integer roomCount;
    @Enumerated(EnumType.STRING)
    private Type type;
    @ElementCollection
    @CollectionTable(name = "house_photos", joinColumns = @JoinColumn(name = "house_id"))
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
