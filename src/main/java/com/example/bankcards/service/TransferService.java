package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Transfer createTransfer(Long userId, Long fromCardId, Long toCardId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getRoles().stream().noneMatch(role -> role.getName().name().equals("USER"))) {
            throw new RuntimeException("У пользователя нет прав для перевода");
        }

        Card fromCard = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new RuntimeException("Карта отправителя не найдена"));

        Card toCard = cardRepository.findById(toCardId)
                .orElseThrow(() -> new RuntimeException("Карта получателя не найдена"));

        if (!fromCard.getOwner().getId().equals(userId) || !toCard.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Переводы возможны только между своими картами");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        Transfer transfer = Transfer.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        return transferRepository.save(transfer);

    }
    public TransferResponse transferBetweenCards(TransferRequest request) {
        Transfer transfer = createTransfer(
                request.getUserId(),
                request.getFromCardId(),
                request.getToCardId(),
                request.getAmount()
        );
        return mapToResponse(transfer);
    }


    private TransferResponse mapToResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getFromCard().getId(),
                transfer.getToCard().getId(),
                transfer.getAmount(),
                transfer.getCreatedAt()
        );
    }
}
