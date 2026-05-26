package com.laundry.system.repository;

import com.laundry.system.entity.Role;
import com.laundry.system.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByName_ShouldReturnRole_WhenExists() {
        Role savedRole = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());

        Optional<Role> found = roleRepository.findByName("ROLE_ADMIN");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenNotExists() {
        Optional<Role> found = roleRepository.findByName("ROLE_NONEXISTENT");
        assertThat(found).isEmpty();
    }

    @Test
    void saveRole_ShouldPersist() {
        Role role = roleRepository.save(Role.builder().name("ROLE_STAFF").build());
        assertThat(role.getId()).isNotNull();
        assertThat(role.getName()).isEqualTo("ROLE_STAFF");
    }
}
