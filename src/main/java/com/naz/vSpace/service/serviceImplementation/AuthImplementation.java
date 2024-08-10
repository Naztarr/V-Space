package com.naz.vSpace.service.serviceImplementation;

import com.naz.vSpace.dto.LoginDto;
import com.naz.vSpace.dto.ResetPasswordDto;
import com.naz.vSpace.dto.SignupDto;
import com.naz.vSpace.entity.User;
import com.naz.vSpace.enums.Role;
import com.naz.vSpace.enums.VerifyType;
import com.naz.vSpace.exception.VSpaceException;
import com.naz.vSpace.mapper.UserMapper;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.UserData;
import com.naz.vSpace.payload.UserResponse;
import com.naz.vSpace.repository.UserRepository;
import com.naz.vSpace.service.AuthenticationService;
import com.naz.vSpace.service.EmailProducerService;
import com.naz.vSpace.util.ForgotPasswordTemplate;
import com.naz.vSpace.util.SignupEmailTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthImplementation implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtImplementation jwtImplementation;
    private final EmailProducerService emailProducerService;

    private final Long expire = 900000L;
    protected String generateToken(User user, Long expiryDate) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("first_name", user.getFirstName());
        claims.put("last_name", user.getLastName());
        return jwtImplementation.generateJwtToken(claims, user.getEmail(), expiryDate);
    }


    /**
     * Registers a new user.
     *
     * @param signupDto The DTO containing user signup information.
     * @return ResponseEntity containing ApiResponse with a message indicating successful signup, requesting for email verification or error message if the signup failed.
     * @throws VSpaceException if the provided email address already exists or if the passwords do not match.
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> signup(SignupDto signupDto) {
        Optional<User> userOptional = userRepository.findByEmail(signupDto.email());
        if(userOptional.isPresent()){
            throw new VSpaceException("Email Address already exists");
        } else if(signupDto.password().equals(signupDto.confirmPassword())){
            User user = UserMapper.mapToUser(signupDto, new User());
            user.setPassword(passwordEncoder.encode(signupDto.password()));
            user.setUserRole(Role.USER);
            User savedUser = userRepository.save(user);

            if(savedUser == null){
                return new ResponseEntity<>(new ApiResponse<>("Registration unsuccessful",
                        HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }

            emailProducerService.sendEmailMessage(signupDto.email(), "Verify your email address",
                    SignupEmailTemplate.signup(signupDto.firstName(),
                    generateToken(savedUser, expire)));
        } else{
            throw new VSpaceException("Provided passwords do not match");
        }
        return ResponseEntity.ok(new ApiResponse<>("Check your email for verification link",
                HttpStatus.OK));
    }

    /**
     * @param token The token for verifying the email
     * @return ResponseEntity containing APiResponse with message indicating success or error accordingly
     */
    @Override
    public ResponseEntity<ApiResponse<String>> confirmEmail(String token) {
        String email = jwtImplementation.extractEmailAddressFromToken(token);
        if(email != null){
            if(jwtImplementation.isExpired(token)){
                throw new VSpaceException("Link has expired. Please request for a new link");
            } else{
                User user = userRepository.findByEmail(email).orElseThrow(()
                        -> new VSpaceException("User not found"));
                if(!user.isEnabled()){
                    user.setIsEnabled(true);
                    userRepository.save(user);

                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    UserResponse userResponse = new UserResponse();
                    userResponse.setAccessToken(generateToken(user, null));
                    userResponse.setUserData(UserMapper.mapToUserData(user, new UserData()));
                    return ResponseEntity.ok(new ApiResponse<>(String.format(
                            "Welcome! '%s'. You have successfully signed up", user.getFirstName()), HttpStatus.OK));
                } else{
                    throw new VSpaceException("Your email address is already verified");
                }
            }
        } else{
            throw new VSpaceException("Link is not properly formatted");
        }
    }

    /**
     * Logs in a user with the provided credentials.
     *
     * @param loginDto The DTO containing user login information.
     * @return ResponseEntity containing ApiResponse with LoginResponse indicating successful login or error message if login failed.
     * @throws VSpaceException() if the user with the provided email address is not found.
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse<UserResponse>> login(LoginDto loginDto) {
        Optional<User> userOptional = userRepository.findByEmail(loginDto.emailAddress());
        if(userOptional.isPresent()){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.emailAddress(), loginDto.Password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserResponse userResponse = new UserResponse();
            userResponse.setAccessToken(generateToken(userOptional.get(), null));
            userResponse.setUserData(UserMapper.mapToUserData(userOptional.get(), new UserData()));

            return ResponseEntity.ok(new ApiResponse<>(userResponse, String.format("Welcome back '%s'. You are now logged in", userOptional.get().getFirstName())));
        } else {
            throw new VSpaceException("User not found");
        }
    }

    /**
     * @param email The email address of the user
     * @param type Enum indicating the type of verification; whether signup or password reset
     * @return ResponseEntity containing ApiResponse indicating success or error accordingly
     */
    @Override
    public ResponseEntity<ApiResponse<String>> sendLink(String email, VerifyType type) {
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new VSpaceException("User not found"));
        if(type == VerifyType.SIGNUP){
            if(user.isEnabled()){
                throw new VSpaceException("Email address is already verified");
            } else{
                emailProducerService.sendEmailMessage(email, "Verify your email address",
                        SignupEmailTemplate.signup(user.getFirstName(),
                                generateToken(user, expire)));

                return ResponseEntity.ok(new ApiResponse<>("Check your email for verification link", HttpStatus.OK));
            }
        } else if(type == VerifyType.PASSWORD_RESET){
            emailProducerService.sendEmailMessage(email, "Password reset",
                    ForgotPasswordTemplate.resetPassword(user.getFirstName(), generateToken(user, expire)));

            user.setPasswordRecovery(true);
            userRepository.save(user);
            return ResponseEntity.ok(new ApiResponse<>("Check your email for password reset link", HttpStatus.OK));
        } else{
            throw new VSpaceException("Invalid verification type");
        }
    }

    /**
     * @param token Token from which the email address will be extracted
     * @Param dto ResetPasswordDto containing new password and confirmation
     * @return ResponseEntity containing ApiResponse indicating status of the operation whether successful or not
     */
    @Override
    public ResponseEntity<ApiResponse<String>> resetPassword(String token, ResetPasswordDto dto) {
        String email = jwtImplementation.extractEmailAddressFromToken(token);
        if(email != null){
            if(jwtImplementation.isExpired(token)){
                throw new VSpaceException("Link has expired. Please request for a new link");
            } else{
                User user = userRepository.findByEmail(email).orElseThrow(()
                        -> new VSpaceException("User not found"));
                if(!user.getPasswordRecovery()){
                    throw new VSpaceException("Password reset was not initiated");
                } else{
                    if(dto.newPassword().equals(dto.confirmPassword())){
                        user.setPassword(passwordEncoder.encode(dto.newPassword()));
                        user.setPasswordRecovery(false);
                        userRepository.save(user);
                        return ResponseEntity.ok(new ApiResponse<>("Password reset successfully", HttpStatus.OK));
                    } else{
                        throw new VSpaceException("Passwords do not match");
                    }
                }
            }
        } else{
            throw new VSpaceException("Link not properly formatted");
        }
    }
}
