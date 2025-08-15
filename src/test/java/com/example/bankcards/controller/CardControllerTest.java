package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();

        // Настройка ObjectMapper для LocalDate
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void getCards_shouldReturnList() throws Exception {
        CardResponse card = new CardResponse();
        when(cardService.getCards(anyInt(), anyInt(), any())).thenReturn(List.of(card));

        mockMvc.perform(get("/api/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCardById_shouldReturnCard() throws Exception {
        CardResponse card = new CardResponse();
        when(cardService.getCardById(anyLong())).thenReturn(Optional.of(card));

        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createCard_shouldReturnCreatedCard() throws Exception {
        CardRequest req = new CardRequest(
                "1234567812345678",
                1L,
                LocalDate.now().plusYears(2)
        );
        CardResponse resp = new CardResponse(
                1L,
                "1234********5678",
                "Test Owner",
                LocalDate.now().plusYears(2),
                null,
                BigDecimal.TEN
        );
        when(cardService.createCard(any())).thenReturn(resp);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.owner").value("Test Owner"));
    }

    @Test
    void blockCard_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/cards/1/block"))
                .andExpect(status().isOk());
        verify(cardService).blockCard(1L);
    }

    @Test
    void activateCard_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/cards/1/activate"))
                .andExpect(status().isOk());
        verify(cardService).activateCard(1L);
    }

    @Test
    void deleteCard_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isOk());
        verify(cardService).deleteCard(1L);
    }

    @Test
    void requestBlock_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/cards/1/request-block"))
                .andExpect(status().isOk());
        verify(cardService).requestBlock(1L);
    }
}
