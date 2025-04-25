package homerep.homerepoauth.models.userservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private Status status = Status.CLIENT;
}
