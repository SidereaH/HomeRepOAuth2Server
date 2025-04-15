package homerep.homerepoauth.models.orderservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private Long id;
    private String streetName;
    private String buildingNumber;
    private String apartmentNumber;
    private String cityName;
    private Double longitude;
    private Double latitude;

}
