package com.laundry.system.service.impl;

import com.laundry.system.dto.UserRegistrationDto;
import com.laundry.system.entity.Customer;
import com.laundry.system.entity.Role;
import com.laundry.system.entity.User;
import com.laundry.system.exception.DuplicateResourceException;
import com.laundry.system.exception.ResourceNotFoundException;
import com.laundry.system.repository.CustomerRepository;
import com.laundry.system.repository.RoleRepository;
import com.laundry.system.repository.UserRepository;
import com.laundry.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerCustomer(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("User", "email", dto.getEmail());
        }

        // 1. Fetch or create Customer role
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build()));

        // 2. Build User
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .active(true)
                .roles(new HashSet<>())
                .build();
        user.getRoles().add(customerRole);
        
        User savedUser = userRepository.save(user);

        // 3. Build Customer
        Customer customer = Customer.builder()
                .user(savedUser)
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .build();
        customerRepository.save(customer);

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}
