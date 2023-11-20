package kz.course.controller;

import kz.course.config.CustomUserDetailsService;
import kz.course.dto.Auth.JwtResponse;
import kz.course.dto.Auth.RefreshTokenRequest;
import kz.course.dto.User.UserResponseAfterAuth;
import kz.course.entity.RefreshToken;
import kz.course.exceptions.NotFoundException;
import kz.course.service.RefreshTokenService;
import kz.course.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .orElse(null);

        if (refreshToken != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUserInfo().getUsername());
            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toList());



            UserResponseAfterAuth userResponseAfterAuth = new UserResponseAfterAuth(
                    refreshToken.getUserInfo().getUsername(),
                    refreshToken.getUserInfo().getEmail(),
                    roles
            );

            String accessToken = jwtTokenUtils.generateToken(userDetails);

            JwtResponse jwtResponse = JwtResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .user(userResponseAfterAuth)
                    .build();

            return ResponseEntity.ok(jwtResponse);
        } else {
            throw new NotFoundException("Refresh token does not exist!");
        }
    }


}


