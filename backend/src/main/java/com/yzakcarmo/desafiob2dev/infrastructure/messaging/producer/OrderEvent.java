package com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderEvent(
        String eventId,
        String eventType,
        OffsetDateTime timestamp,
        String tenant,
        String correlationId,
        Payload payload
) {
    public record Payload(
            UUID orderId,
            String externalReference,
            String buyerReference,
            BigDecimal total,
            String status
    ) {}

    public static OrderEvent of(String eventType, String tenant,
                                String correlationId,
                                com.yzakcarmo.desafiob2dev.domain.entity.Order order) {
        return new OrderEvent(
                UUID.randomUUID().toString(),
                eventType,
                OffsetDateTime.now(),
                tenant,
                correlationId != null ? correlationId : UUID.randomUUID().toString(),
                new Payload(
                        order.getId(),
                        order.getExternalReference(),
                        order.getBuyer().getExternalReference(),
                        order.getTotal(),
                        order.getStatus().name()
                )
        );
    }
}