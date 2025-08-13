package com.example.bankcards.service;

import com.example.bankcards.BaseTestConfig;
import com.example.bankcards.entity.*;
import com.example.bankcards.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest extends BaseTestConfig {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    private TransferService transferService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        transferService = new TransferService(transferRepository, cardRepository, userRepository);
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

        User user = new User();
        user.setId(userId);
        Role userRole = new Role();
        userRole.setName(RoleName.USER);
        user.setRoles(Set.of(userRole));

        Card fromCard = new Card();
        fromCard.setId(fromCardId);
        fromCard.setOwner(user);
        fromCard.setBalance(BigDecimal.valueOf(500));

        Card toCard = new Card();
        toCard.setId(toCardId);
        toCard.setOwner(user);
        toCard.setBalance(BigDecimal.valueOf(200));

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
        assertEquals(fromCard.getBalance(), BigDecimal.valueOf(400));
        assertEquals(toCard.getBalance(), BigDecimal.valueOf(300));

        verify(userRepository).findById(userId);
        verify(cardRepository, times(2)).findById(anyLong());
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transferRepository).save(any(Transfer.class));
    }
}
