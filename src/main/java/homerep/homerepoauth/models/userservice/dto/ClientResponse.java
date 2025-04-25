package homerep.homerepoauth.models.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import homerep.homerepoauth.models.userservice.Status;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientResponse {

        private String id;
        private String firstName;
        private String middleName;
        private String lastName;
        private String email;
        private String phone;
        private Status status;

}
