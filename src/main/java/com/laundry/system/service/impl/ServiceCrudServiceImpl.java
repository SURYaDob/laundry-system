package com.laundry.system.service.impl;

import com.laundry.system.entity.Service;
import com.laundry.system.exception.DuplicateResourceException;
import com.laundry.system.exception.ResourceNotFoundException;
import com.laundry.system.repository.ServiceRepository;
import com.laundry.system.service.ServiceCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceCrudServiceImpl implements ServiceCrudService {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceCrudServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
    }

    @Override
    @Transactional
    public Service createService(Service service) {
        if (serviceRepository.findByName(service.getName()).isPresent()) {
            throw new DuplicateResourceException("Service", "name", service.getName());
        }
        return serviceRepository.save(service);
    }

    @Override
    @Transactional
    public Service updateService(Long id, Service details) {
        Service existing = getServiceById(id);
        
        // Ensure name is unique if changed
        if (!existing.getName().equalsIgnoreCase(details.getName()) 
                && serviceRepository.findByName(details.getName()).isPresent()) {
            throw new DuplicateResourceException("Service", "name", details.getName());
        }

        existing.setName(details.getName());
        existing.setDescription(details.getDescription());
        existing.setBasePrice(details.getBasePrice());
        existing.setPricePerKg(details.getPricePerKg());

        return serviceRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        Service existing = getServiceById(id);
        serviceRepository.delete(existing);
    }
}
