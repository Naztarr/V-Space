package com.naz.vSpace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "house_rent")
@Getter
@Setter
public class HouseRent extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "house_id")
    private House house;
    @Column(name = "is_approved")
    private Boolean isApproved;
}
