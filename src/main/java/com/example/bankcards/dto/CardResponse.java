package com.example.bankcards.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.bankcards.entity.CardStatus;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardResponse {
    private Long id;
    private String maskedCardNumber;
    private String owner;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;

    public CardResponse(Long id,
                        String cardNumber,
                        String owner,
                        LocalDate expiryDate,
                        CardStatus status,
                        BigDecimal balance) {
        this.id = id;
        this.maskedCardNumber = cardNumber;
        this.owner = owner;
        this.expiryDate = expiryDate;
        this.status = status;
        this.balance = balance;
    }
}

