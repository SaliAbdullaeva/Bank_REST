package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private Long transactionId;
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
