package com.bakil.springbootsecurity.service.implementation;

import com.bakil.springbootsecurity.domain.AppUser;
import com.bakil.springbootsecurity.repository.UserRepository;
import com.bakil.springbootsecurity.service.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AppUser register(AppUser appUser) {
        return userRepository.save(appUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username);
        if ( appUser == null )return null;

        return new User( appUser.getUsername(), appUser.getPassword(), new ArrayList<>());
    }
}
