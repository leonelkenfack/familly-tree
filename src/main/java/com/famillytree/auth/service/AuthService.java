package com.famillytree.auth.service;

import com.famillytree.auth.dto.AuthRequest;
import com.famillytree.auth.dto.AuthResponse;
import com.famillytree.auth.dto.RefreshTokenRequest;
import com.famillytree.auth.dto.RegisterRequest;
import com.famillytree.auth.exception.AuthException;
import com.famillytree.auth.model.User;
import com.famillytree.auth.repository.UserRepository;
import com.famillytree.auth.security.JwtService;
import com.famillytree.node.model.Node;
import com.famillytree.node.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final NodeRepository nodeRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw AuthException.usernameAlreadyExists(request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AuthException.emailAlreadyExists(request.getEmail());
        }

        // Créer l'utilisateur
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw AuthException.invalidCredentials();
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(AuthException::invalidCredentials);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Récupérer le nœud de base de l'utilisateur
        Node baseNode = nodeRepository.findByUserIdAndBaseNodeIsTrue(user.getId())
                .stream()
                .findFirst()
                .orElse(null);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .baseNode(baseNode)
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            String username = jwtService.extractUsername(refreshToken);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(AuthException::invalidRefreshToken);

            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);
                String newRefreshToken = jwtService.generateRefreshToken(user);
                
                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(newRefreshToken)
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build();
            }
            
            throw AuthException.invalidRefreshToken();
        } catch (Exception e) {
            throw AuthException.invalidRefreshToken();
        }
    }

    public boolean verifyToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
} 