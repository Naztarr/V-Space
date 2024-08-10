package com.naz.vSpace.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String profilePicture;
}
