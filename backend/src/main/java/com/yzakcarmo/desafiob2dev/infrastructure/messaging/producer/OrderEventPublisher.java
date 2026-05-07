package com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer;

import com.yzakcarmo.desafiob2dev.config.RabbitMQConfig;
import com.yzakcarmo.desafiob2dev.domain.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private static final String EVENT_ORDER_CREATED   = "ORDER_CREATED";
    private static final String EVENT_ORDER_CANCELLED = "ORDER_CANCELLED";

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(Order order, String correlationId) {
        publish(EVENT_ORDER_CREATED, order, correlationId);
    }

    public void publishOrderCancelled(Order order, String correlationId) {
        publish(EVENT_ORDER_CANCELLED, order, correlationId);
    }

    private void publish(String eventType, Order order, String correlationId) {
        OrderEvent event = OrderEvent.of(eventType, order.getTenantCode(), correlationId, order);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.FANOUT_EXCHANGE,
                "",
                event,
                message -> {
                    MessageProperties props = message.getMessageProperties();
                    props.setHeader("x-tenant", order.getTenantCode());
                    props.setHeader("x-correlation-id", correlationId);
                    props.setHeader("x-event-type", eventType);
                    props.setHeader("x-retry-count", 0);
                    return message;
                }
        );

        log.info("Evento publicado: type={} order={} tenant={}",
                eventType, order.getExternalReference(), order.getTenantCode());
    }
}