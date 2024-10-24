package com.ecom.service.impl;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
@Autowired
    private UserRepository userRepository;

@Autowired
private PasswordEncoder passwordEncoder;


    @Override
    public UserDtls saveUser(UserDtls user) {
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        user.setRole("ROLE_USER");
        UserDtls saveUser = userRepository.save(user);
        return saveUser;
    }
}
