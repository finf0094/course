package kz.course.config;

import kz.course.entity.User;
import kz.course.exceptions.AuthenticationException;
import kz.course.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(String username)  {
        // Найти пользователя по USERNAME в базе данных
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException(String.format("Пользователь с именем '%s' не найден", username), HttpStatus.UNAUTHORIZED)
                );

        // Создать экземпляр CustomUserDetails на основе данных пользователя
        CustomUserDetails customUserDetails = new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );

        return customUserDetails;
    }
}
