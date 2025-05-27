package com.DriveAuto.user_service.service;

import com.DriveAuto.user_service.Dto.UserRegistrationDto;
import com.DriveAuto.user_service.Dto.UserUpdateDto;
import com.DriveAuto.user_service.UserServiceInterface.UserServiceInterface;
import com.DriveAuto.user_service.model.Role;
import com.DriveAuto.user_service.model.User;
import com.DriveAuto.user_service.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationDto registrationDto) {

        if(userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if(userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }


        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setEmail(registrationDto.getEmail());
        newUser.setRole(Role.USER);

        return userRepository.save(newUser);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> updateUser(Long id, UserUpdateDto userUpdateDto) {
        return userRepository.findById(id)
                .map(existingUser -> {

                    updateIfNotNull(userUpdateDto.getUsername(), existingUser::setUsername);
                    updateIfNotNull(userUpdateDto.getEmail(), existingUser::setEmail);


                    if (userUpdateDto.getPassword() != null) {
                        existingUser.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
                    }

                    return userRepository.save(existingUser);
                });
    }

   
    private <T> void updateIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }


}
