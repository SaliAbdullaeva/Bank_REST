package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.MaskUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCards_noSearch_returnsAll() {
        Card card = buildCard(1L, "1234567890123456");
        List<Card> cards = List.of(card);
        Page<Card> page = new PageImpl<>(cards);

        when(cardRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<CardResponse> responses = cardService.getCards(0, 10, null);

        assertEquals(1, responses.size());
        assertEquals(MaskUtil.maskCardNumber(card.getCardNumber()), responses.get(0).getMaskedCardNumber());
        verify(cardRepository).findAll(any(Pageable.class));
    }

    @Test
    void getCards_withSearch_returnsFiltered() {
        Card card = buildCard(2L, "9876543210987654");
        List<Card> cards = List.of(card);
        Page<Card> page = new PageImpl<>(cards);

        when(cardRepository.findByCardNumberContaining(eq("9876"), any(Pageable.class))).thenReturn(page);

        List<CardResponse> responses = cardService.getCards(0, 10, "9876");

        assertEquals(1, responses.size());
        assertFalse(responses.get(0).getMaskedCardNumber().contains("9876")); // Because masked
        verify(cardRepository).findByCardNumberContaining(eq("9876"), any(Pageable.class));
    }

    @Test
    void getCardById_found_returnsResponse() {
        Card card = buildCard(1L, "1111222233334444");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        Optional<CardResponse> response = cardService.getCardById(1L);

        assertTrue(response.isPresent());
        assertEquals(MaskUtil.maskCardNumber(card.getCardNumber()), response.get().getMaskedCardNumber());
    }

    @Test
    void getCardById_notFound_returnsEmpty() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CardResponse> response = cardService.getCardById(99L);

        assertFalse(response.isPresent());
    }

    @Test
    void createCard_validRequest_savesAndReturnsResponse() {
        User user = User.builder().id(1L).username("user1").build();
        CardRequest request = new CardRequest();
        request.setUserId(1L);
        request.setCardNumber("5555666677778888");
        request.setExpiryDate(LocalDate.now().plusYears(3));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card c = invocation.getArgument(0);
            c.setId(100L);
            return c;
        });

        CardResponse response = cardService.createCard(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(MaskUtil.maskCardNumber(request.getCardNumber()), response.getMaskedCardNumber());
        assertEquals(user.getUsername(), response.getOwner());
        assertEquals(CardStatus.ACTIVE, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getBalance());
    }

    @Test
    void createCard_userNotFound_throws() {
        CardRequest request = new CardRequest();
        request.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cardService.createCard(request));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void blockCard_found_setsStatusBlocked() {
        Card card = buildCard(1L, "1111222233334444");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.blockCard(1L);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void blockCard_notFound_throws() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cardService.blockCard(1L));
        assertEquals("Карта не найдена", ex.getMessage());
    }

    @Test
    void activateCard_found_setsStatusActive() {
        Card card = buildCard(1L, "1111222233334444");
        card.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.activateCard(1L);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void activateCard_notFound_throws() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cardService.activateCard(1L));
        assertEquals("Карта не найдена", ex.getMessage());
    }

    @Test
    void deleteCard_found_deletesCard() {
        Card card = buildCard(1L, "1111222233334444");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.deleteCard(1L);

        verify(cardRepository).delete(card);
    }

    @Test
    void deleteCard_notFound_throws() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cardService.deleteCard(1L));
        assertEquals("Карта не найдена", ex.getMessage());
    }

    @Test
    void requestBlock_found_setsStatusBlockRequested() {
        Card card = buildCard(1L, "1111222233334444");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.requestBlock(1L);

        assertEquals(CardStatus.BLOCK_REQUESTED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void requestBlock_notFound_throws() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cardService.requestBlock(1L));
        assertEquals("Карта не найдена", ex.getMessage());
    }

    // Вспомогательный метод для создания карты
    private Card buildCard(Long id, String cardNumber) {
        User user = User.builder().id(1L).username("testuser").build();
        return Card.builder()
                .id(id)
                .cardNumber(cardNumber)
                .owner(user)
                .expiryDate(LocalDate.now().plusYears(2))
                .status(CardStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .balance(BigDecimal.TEN)
                .build();
    }
}
