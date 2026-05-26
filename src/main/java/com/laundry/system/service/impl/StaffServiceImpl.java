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
import com.laundry.system.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StaffServiceImpl(StaffRepository staffRepository, UserRepository userRepository,
                            RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
    }

    @Override
    @Transactional
    public Staff createStaff(StaffDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("User", "email", dto.getEmail());
        }

        // 1. Fetch or create Staff Role
        Role staffRole = roleRepository.findByName("ROLE_STAFF")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_STAFF").build()));

        // 2. Build and save base User
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .active(true)
                .roles(new HashSet<>())
                .build();
        user.getRoles().add(staffRole);
        
        User savedUser = userRepository.save(user);

        // 3. Build and save Staff details
        Staff staff = Staff.builder()
                .user(savedUser)
                .phoneNumber(dto.getPhoneNumber())
                .specialization(dto.getSpecialization())
                .status("ACTIVE")
                .build();

        return staffRepository.save(staff);
    }

    @Override
    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = getStaffById(id);
        
        // Deactivate associated user instead of hard deletion to maintain audit integrity
        if (staff.getUser() != null) {
            staff.getUser().setActive(false);
            userRepository.save(staff.getUser());
        }
        
        staff.setStatus("INACTIVE");
        staffRepository.save(staff);
    }
}
