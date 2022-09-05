package com.bakil.springbootsecurity.service.implementation;

import com.bakil.springbootsecurity.utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtFilterRequest extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userService;

    public JwtFilterRequest(JwtUtils jwtUtils, UserDetailsService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorisationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        if( authorisationHeader != null && authorisationHeader.startsWith("Bearer") ){
             jwt = authorisationHeader.substring(7);
             try {
                 username = jwtUtils.extractUsername(jwt);
             }catch ( Exception e ){
                 response.sendError(400);
             }

        }
        try{
            if( username != null && SecurityContextHolder.getContext().getAuthentication() == null ){
                UserDetails currentUserDetails = userService.loadUserByUsername( username );
                Boolean tokenIsValid = jwtUtils.validateToken(jwt, currentUserDetails);

                if( tokenIsValid ){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(currentUserDetails, null, currentUserDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );
                    SecurityContextHolder.getContext().setAuthentication( usernamePasswordAuthenticationToken );
                }
                filterChain.doFilter(request, response);
            }
            if( Arrays.asList("/login", "/register").contains( request.getRequestURI() ) ){
                filterChain.doFilter(request, response);
            }
        }catch (Exception e){
            response.setStatus(400);
        }

    }
}
