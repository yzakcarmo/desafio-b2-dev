package com.yzakcarmo.desafiob2dev.infrastructure.messaging.consumer;

import com.yzakcarmo.desafiob2dev.config.RabbitMQConfig;
import com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer.OrderEvent;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationConsumer.class);

    private final Jackson2JsonMessageConverter messageConverter;

    public OrderNotificationConsumer(Jackson2JsonMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDER_NOTIFICATION)
    public void notify(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            OrderEvent event = (OrderEvent) messageConverter.fromMessage(message);

            log.info("[NOTIFICATION] Evento recebido: type={} order={} tenant={} total={}",
                    event.eventType(),
                    event.payload().externalReference(),
                    event.tenant(),
                    event.payload().total());

            // Simula envio de notificação (e-mail, SMS, push, etc.)
            log.info("[NOTIFICATION] Notificação enviada para buyer do pedido: {}",
                    event.payload().externalReference());

            channel.basicAck(deliveryTag, false);

        } catch (MessageConversionException e) {
            log.error("[NOTIFICATION] Mensagem malformada, descartando: {}", e.getMessage());
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[NOTIFICATION] Erro inesperado, descartando mensagem: {}", e.getMessage());
            channel.basicAck(deliveryTag, false);
        }
    }
}