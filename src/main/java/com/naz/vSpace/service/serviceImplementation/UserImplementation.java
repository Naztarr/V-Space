package com.naz.vSpace.service.serviceImplementation;

import com.naz.vSpace.dto.OwnerCredentials;
import com.naz.vSpace.entity.OwnerCredential;
import com.naz.vSpace.entity.User;
import com.naz.vSpace.exception.VSpaceException;
import com.naz.vSpace.mapper.CredentialMapper;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.OwnerCredentialData;
import com.naz.vSpace.repository.OwnerCredentialRepository;
import com.naz.vSpace.repository.UserRepository;
import com.naz.vSpace.service.CloudinaryService;
import com.naz.vSpace.service.UserService;
import com.naz.vSpace.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserImplementation implements UserService {
    private final UserRepository userRepository;
    private final OwnerCredentialRepository ownerCredentialRepository;
    private final CloudinaryService cloudinaryService;
    @Override
    public ResponseEntity<ApiResponse<OwnerCredentialData>> verifyAsOwner(OwnerCredentials details) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        if(!user.isEnabled()){
            return new ResponseEntity<>(new ApiResponse<>("Your account has not been verified",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } else{
            OwnerCredential ownerCredential = new OwnerCredential();
            ownerCredential.setType(details.type());
            ownerCredential.setIdNumber(details.idNumber());
            ownerCredential.setIdFile(cloudinaryService.uploadFile(details.idFile()));
            ownerCredential.setBvn(details.bvn());
            ownerCredential.setOwner(user);
            ownerCredentialRepository.save(ownerCredential);

            return new ResponseEntity<>(new ApiResponse<>(CredentialMapper.mapToData(ownerCredential,
                    new OwnerCredentialData()),"Your request has been sent",
                    HttpStatus.OK), HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<String>> updateProfilePicture(MultipartFile photo) throws IOException {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));

        String uri = cloudinaryService.uploadPhoto(photo);
        user.setProfilePicture(uri);
        userRepository.save(user);

        return new ResponseEntity<>(new ApiResponse<>(uri, "Profile picture successfully updated",
                HttpStatus.OK), HttpStatus.OK);
    }
}
