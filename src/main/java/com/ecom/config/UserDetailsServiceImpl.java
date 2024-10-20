package com.ecom.config;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDtls user  = userRepository.findByEmail(username);

   if(user == null){

      throw  new UsernameNotFoundException("user not found");
        }
        return new CustomUser(user);
    }
}
