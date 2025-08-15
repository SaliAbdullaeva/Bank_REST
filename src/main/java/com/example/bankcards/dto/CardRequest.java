package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {

    @NotBlank(message = "Номер карты обязателен")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    private String cardNumber;

    @NotNull(message = "Владелец обязателен")
    private Long userId;

    @NotNull(message = "Срок действия обязателен")
    @Future(message = "Срок действия должен быть в будущем")
    private LocalDate expiryDate;
}
