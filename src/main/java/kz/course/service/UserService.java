package kz.course.service;

import kz.course.entity.User;
import kz.course.exceptions.NotFoundException;
import kz.course.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String deleteUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id:'%s' not found", userId))
        );
        userRepository.delete(user);
        return "User successfully deleted";
    }

    public void saveYoutubeToken(Long userId, String token) {
        User user = userRepository.findById(userId).get();
        user.setGoogleToken(token);
        userRepository.save(user);
    }

    public String getYoutubeToken(Long userId) {
        User user = userRepository.findById(userId).get();
        return user.getGoogleToken();
    }
}
