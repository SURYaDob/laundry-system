package com.laundry.system.service;

import com.laundry.system.dto.UserRegistrationDto;
import com.laundry.system.entity.User;

public interface UserService {
    User registerCustomer(UserRegistrationDto registrationDto);
    User findByEmail(String email);
}
