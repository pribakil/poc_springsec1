package com.bakil.springbootsecurity.repository;

import com.bakil.springbootsecurity.domain.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<AppUser, String> {
    AppUser findByUsername(String username);
}
