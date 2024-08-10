package com.naz.vSpace.mapper;

import com.naz.vSpace.entity.OwnerCredential;
import com.naz.vSpace.payload.OwnerCredentialData;

public class CredentialMapper {
    public static OwnerCredentialData mapToData(OwnerCredential credential, OwnerCredentialData data){
        data.setId(credential.getId());
        data.setType(credential.getType());
        data.setIdNumber(credential.getIdNumber());
        data.setIdFile(credential.getIdFile());
        data.setBvn(credential.getBvn());
        return data;
    }
}
