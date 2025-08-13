package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.MaskUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public List<CardResponse> getCards(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cardsPage;

        if (search == null || search.isBlank()) {
            cardsPage = cardRepository.findAll(pageable);
        } else {
            cardsPage = cardRepository.findByCardNumberContaining(search, pageable);
        }

        return cardsPage.stream()
                .map(this::mapToCardResponseWithMask)
                .collect(Collectors.toList());
    }

    public Optional<CardResponse> getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .map(this::mapToCardResponseWithMask);
    }

    public CardResponse createCard(CardRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Card card = Card.builder()
                .cardNumber(request.getCardNumber())
                .owner(user)
                .expiryDate(request.getExpiryDate())
                .status(CardStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .build();

        Card saved = cardRepository.save(card);
        return mapToCardResponseWithMask(saved);
    }

    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    public void activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        cardRepository.delete(card);
    }

    public void requestBlock(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus(CardStatus.BLOCK_REQUESTED);
        cardRepository.save(card);
    }

    private CardResponse mapToCardResponseWithMask(Card card) {
        String maskedNumber = MaskUtil.maskCardNumber(card.getCardNumber());
        String ownerName = card.getOwner().getUsername();

        return new CardResponse(
                card.getId(),
                maskedNumber,
                ownerName,
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance()
        );
    }
}
