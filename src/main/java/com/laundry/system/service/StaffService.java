package com.laundry.system.service;

import com.laundry.system.dto.StaffDto;
import com.laundry.system.entity.Staff;
import java.util.List;

public interface StaffService {
    List<Staff> getAllStaff();
    Staff getStaffById(Long id);
    Staff createStaff(StaffDto staffDto);
    void deleteStaff(Long id);
}
