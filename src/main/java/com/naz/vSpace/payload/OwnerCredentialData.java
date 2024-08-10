package com.naz.vSpace.payload;

import com.naz.vSpace.enums.IdType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerCredentialData {
    private UUID id;

    private IdType type;

    private String idNumber;

    private String idFile;

    private String bvn;
}
