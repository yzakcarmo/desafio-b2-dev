package com.yzakcarmo.desafiob2dev.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzakcarmo.desafiob2dev.api.dto.request.ReprocessRequest;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.amqp.core.MessagePostProcessor;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessagingController.class)
class MessagingControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean RabbitTemplate rabbitTemplate;

    private static final String TENANT = "FARMA-DEFAULT";
    private static final String AUTH   = "Bearer test-token";
    private static final String BASE   = "/api/v1/messaging/parking-lot/reprocess";

    @Test
    void reprocess_noMessages_returns200WithZeroCount() throws Exception {
        when(rabbitTemplate.receive(anyString(), anyLong())).thenReturn(null);

        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ReprocessRequest("order.parking-lot", 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reprocessed").value(0))
                .andExpect(jsonPath("$.queue").value("order.parking-lot"));
    }

    @Test
    void reprocess_withMessages_requeuesAndReturnsCount() throws Exception {
        var props = new MessageProperties();
        var msg = new Message("{}".getBytes(), props);

        // Retorna uma mensagem na primeira chamada, null na segunda (sem mais mensagens)
        when(rabbitTemplate.receive(eq("order.parking-lot"), anyLong()))
                .thenReturn(msg)
                .thenReturn(null);

        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ReprocessRequest("order.parking-lot", 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reprocessed").value(1));

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(byte[].class), any(MessagePostProcessor.class));
    }

    @Test
    void reprocess_missingTenantHeader_returns400() throws Exception {
        mockMvc.perform(post(BASE)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ReprocessRequest("order.parking-lot", 10))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TENANT-001"));
    }

    @Test
    void reprocess_missingAuthorizationHeader_returns401() throws Exception {
        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ReprocessRequest("order.parking-lot", 10))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH-001"));
    }

    @Test
    void reprocess_blankQueue_returns422() throws Exception {
        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"queue\":\"\",\"maxMessages\":10}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-001"));
    }

    @Test
    void reprocess_maxMessagesZero_returns422() throws Exception {
        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"queue\":\"order.parking-lot\",\"maxMessages\":0}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-001"));
    }

    @Test
    void reprocess_maxMessagesAboveLimit_returns422() throws Exception {
        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"queue\":\"order.parking-lot\",\"maxMessages\":101}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-001"));
    }
}