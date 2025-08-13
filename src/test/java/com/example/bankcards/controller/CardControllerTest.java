package com.example.bankcards.controller;

import com.example.bankcards.BaseTestConfig;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class CardControllerTest extends BaseTestConfig {

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
    }

    @Test
    void getCards_shouldReturnList() throws Exception {
        CardResponse card = new CardResponse();
        // заполни card при необходимости
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
        when(cardService.getCardById(anyLong())).thenReturn(java.util.Optional.of(card));

        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createCard_shouldReturnCreatedCard() throws Exception {
        CardRequest req = new CardRequest();
        CardResponse resp = new CardResponse();
        when(cardService.createCard(any())).thenReturn(resp);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void blockCard_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/cards/1/block"))
                .andExpect(status().isOk());
        Mockito.verify(cardService).blockCard(1L);
    }

    @Test
    void activateCard_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/cards/1/activate"))
                .andExpect(status().isOk());
        Mockito.verify(cardService).activateCard(1L);
    }

    @Test
    void deleteCard_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isOk());
        Mockito.verify(cardService).deleteCard(1L);
    }

    @Test
    void requestBlock_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/cards/1/request-block"))
                .andExpect(status().isOk());
        Mockito.verify(cardService).requestBlock(1L);
    }
}
