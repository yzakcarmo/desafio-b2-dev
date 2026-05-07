package com.yzakcarmo.desafiob2dev.api.controller;

import com.yzakcarmo.desafiob2dev.api.dto.request.ReprocessRequest;
import com.yzakcarmo.desafiob2dev.config.RabbitMQConfig;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/messaging")
public class MessagingController {

    private static final Logger log = LoggerFactory.getLogger(MessagingController.class);

    private final RabbitTemplate rabbitTemplate;

    public MessagingController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/parking-lot/reprocess")
    public ResponseEntity<Map<String, Object>> reprocess(
            @RequestBody @Valid ReprocessRequest request) {

        int reprocessed = 0;

        for (int i = 0; i < request.maxMessages(); i++) {
            Message message = rabbitTemplate.receive(request.queue(), 1000);
            if (message == null) break;

            // Reseta o retry-count e republica na fila principal
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.FANOUT_EXCHANGE,
                    "",
                    message.getBody(),
                    msg -> {
                        msg.getMessageProperties()
                                .getHeaders()
                                .putAll(message.getMessageProperties().getHeaders());
                        msg.getMessageProperties().setHeader("x-retry-count", 0);
                        return msg;
                    }
            );

            log.info("Mensagem reprocessada do parking lot: queue={}", request.queue());
            reprocessed++;
        }

        return ResponseEntity.ok(Map.of(
                "queue", request.queue(),
                "reprocessed", reprocessed
        ));
    }
}