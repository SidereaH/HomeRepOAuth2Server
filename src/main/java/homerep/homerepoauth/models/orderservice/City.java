package homerep.homerepoauth.models.orderservice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//отдельный микро
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class City {

    private Long id;
    private String name;
    private String country;
    private String state;
    private int avaliableMaster;

}
