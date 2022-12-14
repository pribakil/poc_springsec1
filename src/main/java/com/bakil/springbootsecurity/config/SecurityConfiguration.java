package com.bakil.springbootsecurity.config;

import com.bakil.springbootsecurity.service.implementation.JwtFilterRequest;
import com.bakil.springbootsecurity.service.implementation.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private  final UserServiceImpl userService;
    private final JwtFilterRequest jwtFilterRequest;

    public SecurityConfiguration(UserServiceImpl userService, JwtFilterRequest jwtFilterRequest) {
        this.userService = userService;
        this.jwtFilterRequest = jwtFilterRequest;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService( userService );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/register", "/login", "/tokenMismatch")
                .permitAll()
                .anyRequest()
                .authenticated();
                /*.and()
                .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS );*/
        http.addFilterBefore( jwtFilterRequest, UsernamePasswordAuthenticationFilter.class );
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
