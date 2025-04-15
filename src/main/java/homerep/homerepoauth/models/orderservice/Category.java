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

public class Category {
    private Long id;
    private String name;
    private String description;
    private Long bestEmployee_id;
}
