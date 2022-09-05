package com.bakil.springbootsecurity.service;

import com.bakil.springbootsecurity.domain.AppUser;

public interface UserService {
    AppUser register(AppUser appUser);
}
