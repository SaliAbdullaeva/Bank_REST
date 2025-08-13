package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/*
Обрабатывает запросы на переводы между картами.
Принимает TransferRequest с проверками.
Вызывает TransferService для выполнения бизнес-логики перевода.
Возвращает TransferResponse с результатом операции.
*/

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransferResponse> transfer(@Validated @RequestBody TransferRequest request) {
        TransferResponse response = transferService.transferBetweenCards(request);
        return ResponseEntity.ok(response);
    }
}
