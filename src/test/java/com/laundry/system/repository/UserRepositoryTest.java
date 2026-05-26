package com.laundry.system.repository;

import com.laundry.system.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build());

        testUser = userRepository.save(User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .active(true)
                .roles(Set.of(testRole))
                .build());
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getFullName()).isEqualTo("John Doe");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenExists() {
        boolean exists = userRepository.existsByEmail("test@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenNotExists() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    void saveUser_ShouldPersistWithRoles() {
        User newUser = userRepository.save(User.builder()
                .email("new@example.com")
                .password("password")
                .firstName("Jane")
                .lastName("Smith")
                .active(true)
                .roles(Set.of(testRole))
                .build());

        assertThat(newUser.getId()).isNotNull();
        assertThat(newUser.getRoles()).hasSize(1);
        assertThat(newUser.getRoles().iterator().next().getName()).isEqualTo("ROLE_CUSTOMER");
    }
}
