package homerep.homerepoauth.models.orderservice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor//(access = AccessLevel.PRIVATE, force = true)

public class Order {

    private Long id;
    private String description;
    private Category category;
    private Long customerId;
    private Long employeeId;
    private Address address;
    private PaymentType paymentType;
    private Boolean accepted = false;

}
