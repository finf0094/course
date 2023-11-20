package kz.course.controller;

import kz.course.config.CustomUserDetails;
import kz.course.config.CustomUserDetailsService;
import kz.course.dto.Auth.AuthRequest;
import kz.course.dto.Auth.JwtResponse;
import kz.course.dto.Auth.RegistrationUserDTO;
import kz.course.dto.User.UserResponseAfterAuth;
import kz.course.entity.RefreshToken;
import kz.course.entity.User;
import kz.course.exceptions.AuthenticationException;
import kz.course.service.RefreshTokenService;
import kz.course.service.RoleService;
import kz.course.service.UserService;
import kz.course.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final RoleService roleService;


    @PostMapping("/register")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDTO registrationUserDto) {
        Optional<User> user = userService.getUserByUsername(registrationUserDto.getUsername());

        if (user.isPresent()) {
            throw new AuthenticationException(String.format("User with username: '%s' exist", registrationUserDto.getUsername()), HttpStatus.BAD_REQUEST);
        }

        if (userService.getUserByEmail(registrationUserDto.getEmail()).isPresent()) {
            throw new AuthenticationException(
                    String.format("Пользователь с почтой '%s' уже существует", registrationUserDto.getEmail()),
                    HttpStatus.BAD_REQUEST
            );
        }

        User newUser = new User();
        newUser.setUsername(registrationUserDto.getUsername());
        newUser.setEmail(registrationUserDto.getEmail());
        newUser.setRoles(List.of(roleService.findByName("ROLE_USER")));
        newUser.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));

        userService.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("User successfully created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), authRequest.getPassword()
            ));
        }  catch (BadCredentialsException e) {
            throw new AuthenticationException("Password wrong", HttpStatus.UNAUTHORIZED);
        }

        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String accessToken = jwtTokenUtils.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername());

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        UserResponseAfterAuth userResponseAfterAuth = new UserResponseAfterAuth(authRequest.getUsername(), userDetails.getEmail(), roles);

        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userResponseAfterAuth)
                .build();

        return ResponseEntity.ok(jwtResponse);
    }

}
