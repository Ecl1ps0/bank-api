package com.bank_api.bank.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bank_api.bank.models.User;
import com.bank_api.bank.repository.UserRepository;

@Service
@Primary
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findById(String id) {
        return this.userRepository.findById(UUID.fromString(id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findByPhoneNumber(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User does not exist");
        }

        return user.get();
    }

}
