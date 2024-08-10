package com.naz.vSpace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "land_rent")
@Getter
@Setter
public class LandRent extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "land_id")
    private Land land;
    @Column(name = "is_approved")
    private Boolean isApproved;
}
