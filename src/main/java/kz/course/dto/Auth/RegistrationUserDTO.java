package kz.course.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationUserDTO {
    private String username;
    private String password;
    private String email;
}
