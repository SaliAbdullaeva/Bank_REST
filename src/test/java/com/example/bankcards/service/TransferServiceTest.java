package com.example.bankcards.service;

import com.example.bankcards.entity.*;
import com.example.bankcards.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransferService transferService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createTransfer_success() {
        Long userId = 1L;
        Long fromCardId = 10L;
        Long toCardId = 20L;
        BigDecimal amount = BigDecimal.valueOf(100);

        User user = buildUser(userId);
        Card fromCard = buildCard(fromCardId, user, BigDecimal.valueOf(500));
        Card toCard = buildCard(toCardId, user, BigDecimal.valueOf(200));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(i -> i.getArgument(0));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

        Transfer transfer = transferService.createTransfer(userId, fromCardId, toCardId, amount);

        assertNotNull(transfer);
        assertEquals(fromCard, transfer.getFromCard());
        assertEquals(toCard, transfer.getToCard());
        assertEquals(amount, transfer.getAmount());
        assertEquals(BigDecimal.valueOf(400), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(300), toCard.getBalance());

        verify(userRepository).findById(userId);
        verify(cardRepository, times(2)).findById(anyLong());
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transferRepository).save(any(Transfer.class));
    }

    // Вспомогательные методы
    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        Role role = new Role();
        role.setName(RoleName.USER);
        user.setRoles(Set.of(role));
        return user;
    }

    private Card buildCard(Long id, User owner, BigDecimal balance) {
        Card card = new Card();
        card.setId(id);
        card.setOwner(owner);
        card.setBalance(balance);
        return card;
    }
}
