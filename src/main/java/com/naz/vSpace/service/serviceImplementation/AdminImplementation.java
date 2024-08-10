package com.naz.vSpace.service.serviceImplementation;

import com.naz.vSpace.dto.AdminDto;
import com.naz.vSpace.entity.OwnerCredential;
import com.naz.vSpace.entity.User;
import com.naz.vSpace.enums.Role;
import com.naz.vSpace.exception.VSpaceException;
import com.naz.vSpace.mapper.UserMapper;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.UserData;
import com.naz.vSpace.repository.OwnerCredentialRepository;
import com.naz.vSpace.repository.UserRepository;
import com.naz.vSpace.service.AdminService;
import com.naz.vSpace.service.EmailProducerService;
import com.naz.vSpace.util.AdminPasswordGenerator;
import com.naz.vSpace.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AdminImplementation implements AdminService {
    private final OwnerCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final EmailProducerService emailProducerService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ApiResponse<String>> verifyOwner(UUID credentialId, String ownerEmail) {
        User user = userRepository.findByEmail(ownerEmail).orElseThrow(
                () -> new VSpaceException("User does not exist"));
        OwnerCredential credential = credentialRepository.findById(credentialId).orElseThrow(
                () -> new VSpaceException("Credential does not exist"));
        if(user.equals(credential.getOwner())){
            user.setIsOwnerVerified(true);
            user.setUserRole(Role.LESSOR);
            userRepository.save(user);

            emailProducerService.sendEmailMessage(user.getEmail(), "You're an owner",
                    String.format("Congratulations %s, %n%n You have now been verified " +
                            "as an owner in V-Space", user.getFirstName()));

            return new ResponseEntity<>(new ApiResponse<>("User is now owner-verified",
                    HttpStatus.OK), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(new ApiResponse<>("User can not be verified with the credentials",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);

        }
    }

    @Override
    public ResponseEntity<ApiResponse<UserData>> createAdmin(AdminDto dto) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("user not found"));
        Optional<User> adminCheck = userRepository.findByEmail(dto.getEmail());
        if(adminCheck.isPresent()){
            throw new VSpaceException("User with this email already exists");
        }
        User admin = new User();
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());
        admin.setEmail(dto.getEmail());
        admin.setPhoneNumber(dto.getPhoneNumber());
        admin.setContactAddress(dto.getContactAddress());
        admin.setPassword(passwordEncoder.encode(AdminPasswordGenerator.generatePassword()));
        admin.setUserRole(Role.ADMIN);

        User savedAdmin = userRepository.save(admin);
        return new ResponseEntity<>(new ApiResponse<>(UserMapper.mapToUserData(savedAdmin, new UserData()),
                "Admin successfully created", HttpStatus.OK), HttpStatus.OK);
    }
}
