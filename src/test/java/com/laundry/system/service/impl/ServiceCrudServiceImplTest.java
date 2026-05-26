package com.laundry.system.service.impl;

import com.laundry.system.entity.Service;
import com.laundry.system.exception.DuplicateResourceException;
import com.laundry.system.exception.ResourceNotFoundException;
import com.laundry.system.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCrudServiceImplTest {

    @Mock
    private ServiceRepository serviceRepository;

    private ServiceCrudServiceImpl serviceCrudService;

    @BeforeEach
    void setUp() {
        serviceCrudService = new ServiceCrudServiceImpl(serviceRepository);
    }

    @Test
    void getAllServices_ShouldReturnAll() {
        Service s1 = Service.builder().id(1L).name("Wash").build();
        Service s2 = Service.builder().id(2L).name("Iron").build();
        when(serviceRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Service> result = serviceCrudService.getAllServices();

        assertThat(result).hasSize(2);
    }

    @Test
    void getServiceById_ShouldReturnService_WhenExists() {
        Service service = Service.builder().id(1L).name("Dry Clean").basePrice(100.0).pricePerKg(50.0).build();
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        Service result = serviceCrudService.getServiceById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Dry Clean");
    }

    @Test
    void getServiceById_ShouldThrowException_WhenNotFound() {
        when(serviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceCrudService.getServiceById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Service not found with id: '99'");
    }

    @Test
    void createService_ShouldSave_WhenNameIsUnique() {
        Service newService = Service.builder().name("Premium Wash").basePrice(150.0).pricePerKg(40.0).build();
        when(serviceRepository.findByName("Premium Wash")).thenReturn(Optional.empty());
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> {
            Service s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        Service result = serviceCrudService.createService(newService);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(serviceRepository).save(newService);
    }

    @Test
    void createService_ShouldThrowException_WhenNameAlreadyExists() {
        Service existing = Service.builder().name("Wash").build();
        Service newService = Service.builder().name("Wash").build();
        when(serviceRepository.findByName("Wash")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> serviceCrudService.createService(newService))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Service already exists with name: 'Wash'");

        verify(serviceRepository, never()).save(any());
    }

    @Test
    void updateService_ShouldUpdate_WhenNameIsUniqueOrSame() {
        Service existing = Service.builder().id(1L).name("Wash").basePrice(50.0).pricePerKg(30.0).description("Old desc").build();
        Service details = Service.builder().name("Premium Wash").basePrice(80.0).pricePerKg(40.0).description("Updated desc").build();

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(serviceRepository.findByName("Premium Wash")).thenReturn(Optional.empty());
        when(serviceRepository.save(any(Service.class))).thenReturn(existing);

        Service result = serviceCrudService.updateService(1L, details);

        assertThat(result.getName()).isEqualTo("Premium Wash");
        assertThat(result.getBasePrice()).isEqualTo(80.0);
        assertThat(result.getPricePerKg()).isEqualTo(40.0);
    }

    @Test
    void deleteService_ShouldDelete_WhenExists() {
        Service service = Service.builder().id(1L).name("Test").build();
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        serviceCrudService.deleteService(1L);

        verify(serviceRepository).delete(service);
    }

    @Test
    void deleteService_ShouldThrowException_WhenNotFound() {
        when(serviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceCrudService.deleteService(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
