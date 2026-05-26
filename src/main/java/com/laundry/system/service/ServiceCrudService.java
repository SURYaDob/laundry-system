package com.laundry.system.service;

import com.laundry.system.entity.Service;
import java.util.List;

public interface ServiceCrudService {
    List<Service> getAllServices();
    Service getServiceById(Long id);
    Service createService(Service service);
    Service updateService(Long id, Service serviceDetails);
    void deleteService(Long id);
}
