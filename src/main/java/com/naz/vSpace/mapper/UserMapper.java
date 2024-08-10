package com.naz.vSpace.mapper;

import com.naz.vSpace.dto.SignupDto;
import com.naz.vSpace.entity.User;
import com.naz.vSpace.payload.UserData;

public class UserMapper {
    public static User mapToUser(SignupDto dto, User user){
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPhoneNumber(dto.phoneNumber());
        user.setContactAddress(dto.contactAddress());

        return user;
    }
    public static UserData mapToUserData(User user, UserData userData){
        userData.setFirstName(user.getFirstName());
        userData.setLastName(user.getLastName());
        userData.setEmail(user.getEmail());
        userData.setPhone(user.getPhoneNumber());
        userData.setAddress(user.getContactAddress());
        userData.setProfilePicture(user.getProfilePicture());

        return userData;
    }
}
