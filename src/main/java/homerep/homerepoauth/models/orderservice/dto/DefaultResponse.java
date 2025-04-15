package homerep.homerepoauth.models.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DefaultResponse<T, String> {
    private T object;  // Переменная любого типа
    private String message;
}
