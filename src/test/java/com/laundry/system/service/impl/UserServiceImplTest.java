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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository, customerRepository, passwordEncoder);
    }

    @Test
    void registerCustomer_ShouldCreateUserAndCustomer_WhenEmailIsUnique() {
        UserRegistrationDto dto = UserRegistrationDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .build();

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(Optional.of(
                Role.builder().id(1L).name("ROLE_CUSTOMER").build()));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        User result = userService.registerCustomer(dto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@example.com");

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).save(userCaptor.capture());
        verify(customerRepository).save(customerCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getFirstName()).isEqualTo("John");
        assertThat(savedUser.getLastName()).isEqualTo("Doe");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");

        Customer savedCustomer = customerCaptor.getValue();
        assertThat(savedCustomer.getPhoneNumber()).isEqualTo("1234567890");
        assertThat(savedCustomer.getAddress()).isEqualTo("123 Main St");
    }

    @Test
    void registerCustomer_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRegistrationDto dto = UserRegistrationDto.builder()
                .email("existing@example.com")
                .build();

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerCustomer(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User already exists with email: 'existing@example.com'");

        verify(userRepository, never()).save(any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .firstName("John")
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("john@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findByEmail_ShouldThrowException_WhenNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with email: 'unknown@example.com'");
    }
}
