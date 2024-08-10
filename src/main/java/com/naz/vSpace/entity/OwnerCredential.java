package com.naz.vSpace.entity;

import com.naz.vSpace.enums.IdType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "credentials")
@Getter
@Setter
public class OwnerCredential extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private IdType type;
    @Column(name = "id_number")
    private String idNumber;
    @Column(name = "id_file")
    private String idFile;
    @Column(name = "bvn")
    private String bvn;
    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
