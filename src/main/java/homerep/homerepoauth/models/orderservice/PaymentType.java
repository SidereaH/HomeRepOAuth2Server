package homerep.homerepoauth.models.orderservice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pay_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false, unique = true)
    private String name;
    private String description;
    private Boolean isActive = false;
}
