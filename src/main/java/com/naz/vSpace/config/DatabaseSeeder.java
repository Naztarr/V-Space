package com.naz.vSpace.config;

import com.naz.vSpace.entity.User;
import com.naz.vSpace.enums.Role;
import com.naz.vSpace.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(userRepository.count() == 0){
            seedDatabase();
        } else{
            System.out.println("database is not empty::::::::::Skipping seeding");
        }
    }

    public void seedDatabase(){
        User superadmin = new User();
        superadmin.setFirstName("Naz");
        superadmin.setLastName("Star");
        superadmin.setContactAddress("Bariga, Lagos, nigeria");
        superadmin.setPhoneNumber("9037136349");
        superadmin.setEmail("herbertemmanuel116@gmail.com");
        superadmin.setUserRole(Role.SUPERADMIN);
        superadmin.setPassword(passwordEncoder.encode("Naztarr123$"));
        superadmin.setProfilePicture("https://drive.usercontent.google.com/download?" +
                "id=14TfkihQAgo2-M-1lXKINXF5L8iUE8Z_n&export=download&authuser=0&confirm" +
                "=t&uuid=47d6fcd5-fb00-4602-8791-54566bcab687&at=APZUnTXwxagvDoRCpM_n14FEiJz3:1707852575841");

        User superadmin2 = new User();
        superadmin2.setFirstName("Aza");
        superadmin2.setLastName("Hemachael");
        superadmin2.setContactAddress("Shomolu, Lagos, nigeria");
        superadmin2.setPhoneNumber("9156248851");
        superadmin2.setEmail("hemachael18@gmail.com");
        superadmin2.setUserRole(Role.SUPERADMIN);
        superadmin2.setPassword(passwordEncoder.encode("Hemachael123$"));
        superadmin2.setProfilePicture("https://media.licdn.com/dms/image/v2/D4D03AQH0QG2HtMd84A/" +
                "profile-displayphoto-shrink_800_800/profile-displayphoto-shrink_800_800/0/1715165647746?e=1728" +
                "518400&v=beta&t=PSKMvIETH3fHAQwWeBymP_az1Bbj07n_qOPaP0fu4jQ");

        userRepository.saveAll(Arrays.asList(superadmin, superadmin2));
    }

}
