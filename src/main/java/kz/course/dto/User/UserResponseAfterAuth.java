package kz.course.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UserResponseAfterAuth {
    private String username;
    private String email;
    private List<String> roles;
}
