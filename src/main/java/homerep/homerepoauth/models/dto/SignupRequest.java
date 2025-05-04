package homerep.homerepoauth.models.dto;

import homerep.homerepoauth.models.userservice.Status;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String username;
    private String email;
    private String phone;
    private String password;
    private Status status;
}
