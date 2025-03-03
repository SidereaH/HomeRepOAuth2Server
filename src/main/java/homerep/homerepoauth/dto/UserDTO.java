package homerep.homerepoauth.dto;

import homerep.homerepoauth.models.User;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private final String username;
    private final String email;
    private final String phone;
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getUsername(), user.getEmail(), user.getPhone());
    }
}
