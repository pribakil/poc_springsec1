package com.bakil.springbootsecurity.controller;

import com.bakil.springbootsecurity.domain.AppUser;
import com.bakil.springbootsecurity.model.AuthenticationRequest;
import com.bakil.springbootsecurity.model.AuthenticationResponse;
import com.bakil.springbootsecurity.service.UserService;
import com.bakil.springbootsecurity.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    private ResponseEntity<?> registerUser(@RequestBody AuthenticationRequest authenticationRequest){
        AppUser appUser = new AppUser( authenticationRequest.getUsername(), authenticationRequest.getPassword() );
        try {
            userService.register(appUser);
        }catch (Exception ex){
            return ResponseEntity.ok(new AuthenticationResponse( "Registration not ok : "+ appUser.getUsername() ));
        }
        return ResponseEntity.ok(new AuthenticationResponse( "Registration is ok : "+ appUser.getUsername() ));
    }

    @PostMapping("/login")
    private ResponseEntity<?> loginUser(@RequestBody AuthenticationRequest authenticationRequest){
        try{
            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken( authenticationRequest.getUsername(), authenticationRequest.getPassword() ));
        }catch (Exception e){
            return ResponseEntity.ok( new AuthenticationResponse( "Authentication not ok : "+ authenticationRequest.getUsername() ) );
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String generatedToken = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok( new AuthenticationResponse( generatedToken ) );
    }
}
