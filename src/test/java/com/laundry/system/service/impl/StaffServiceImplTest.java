package com.laundry.system.service.impl;

import com.laundry.system.dto.StaffDto;
import com.laundry.system.entity.Role;
import com.laundry.system.entity.Staff;
import com.laundry.system.entity.User;
import com.laundry.system.exception.DuplicateResourceException;
import com.laundry.system.exception.ResourceNotFoundException;
import com.laundry.system.repository.RoleRepository;
import com.laundry.system.repository.StaffRepository;
import com.laundry.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Staff> staffCaptor;

    private StaffServiceImpl staffService;

    @BeforeEach
    void setUp() {
        staffService = new StaffServiceImpl(staffRepository, userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void getAllStaff_ShouldReturnAllStaff() {
        Staff staff1 = Staff.builder().id(1L).build();
        Staff staff2 = Staff.builder().id(2L).build();
        when(staffRepository.findAll()).thenReturn(List.of(staff1, staff2));

        List<Staff> result = staffService.getAllStaff();

        assertThat(result).hasSize(2);
    }

    @Test
    void getStaffById_ShouldReturnStaff_WhenExists() {
        Staff staff = Staff.builder().id(1L).status("ACTIVE").build();
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));

        Staff result = staffService.getStaffById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void getStaffById_ShouldThrowException_WhenNotFound() {
        when(staffRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.getStaffById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Staff not found with id: '99'");
    }

    @Test
    void createStaff_ShouldCreateUserAndStaff() {
        StaffDto dto = StaffDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .specialization("Ironing")
                .build();

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_STAFF")).thenReturn(Optional.of(
                Role.builder().id(2L).name("ROLE_STAFF").build()));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> {
            Staff s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        Staff result = staffService.createStaff(dto);

        assertThat(result).isNotNull();
        verify(userRepository).save(userCaptor.capture());
        verify(staffRepository).save(staffCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getFirstName()).isEqualTo("Jane");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");

        Staff savedStaff = staffCaptor.getValue();
        assertThat(savedStaff.getSpecialization()).isEqualTo("Ironing");
    }

    @Test
    void createStaff_ShouldThrowException_WhenEmailExists() {
        StaffDto dto = StaffDto.builder().email("existing@example.com").build();
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> staffService.createStaff(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("User already exists with email: 'existing@example.com'");
    }

    @Test
    void deleteStaff_ShouldDeactivateStaffAndUser() {
        User user = User.builder().id(1L).active(true).build();
        Staff staff = Staff.builder().id(1L).user(user).status("ACTIVE").build();

        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));

        staffService.deleteStaff(1L);

        assertThat(staff.getStatus()).isEqualTo("INACTIVE");
        assertThat(user.getActive()).isFalse();
        verify(userRepository).save(user);
        verify(staffRepository).save(staff);
    }
}
