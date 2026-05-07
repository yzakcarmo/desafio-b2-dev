package com.yzakcarmo.desafiob2dev.infrastructure.messaging.consumer;

import com.yzakcarmo.desafiob2dev.config.RabbitMQConfig;
import com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer.OrderEvent;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderProcessConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessConsumer.class);

    @Value("${messaging.rabbitmq.max-retries:3}")
    private int maxRetries;

    private final RabbitTemplate rabbitTemplate;
    private final Jackson2JsonMessageConverter messageConverter;

    public OrderProcessConsumer(RabbitTemplate rabbitTemplate,
                                Jackson2JsonMessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = messageConverter;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDER_PROCESS)
    public void process(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            OrderEvent event = (OrderEvent) messageConverter.fromMessage(message);
            log.info("Processando evento: type={} order={} tenant={}",
                    event.eventType(), event.payload().externalReference(), event.tenant());

            // Simula processamento que pode falhar (ex: chamada a serviço externo)
            simulateExternalServiceCall(event);

            channel.basicAck(deliveryTag, false);
            log.info("Evento processado com sucesso: {}", event.payload().externalReference());

        } catch (MessageConversionException e) {
            // Mensagem malformada — descarta sem retry para não bloquear a fila
            log.error("Mensagem malformada, descartando: {}", e.getMessage());
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            handleFailure(message, channel, deliveryTag, e);
        }
    }

    private void handleFailure(Message message, Channel channel,
                               long deliveryTag, Exception e) throws IOException {
        MessageProperties props = message.getMessageProperties();
        int retryCount = getRetryCount(props);

        log.warn("Falha ao processar mensagem. retry-count={} erro={}", retryCount, e.getMessage());

        if (retryCount >= maxRetries) {
            // Esgotou tentativas — envia para o Parking Lot
            log.error("Máximo de retries atingido ({}). Enviando para parking lot.", maxRetries);
            sendToParkingLot(message, retryCount);
            channel.basicAck(deliveryTag, false);
        } else {
            // Incrementa contador e envia para DLQ — voltará após TTL
            incrementRetryAndSendToDlq(message, retryCount);
            channel.basicAck(deliveryTag, false);
        }
    }

    private void simulateExternalServiceCall(OrderEvent event) {
        // Simula falha para eventos com referência terminando em "FAIL"
        if (event.payload().externalReference().endsWith("FAIL")) {
            throw new RuntimeException("Serviço externo indisponível (simulado)");
        }
    }

    private int getRetryCount(MessageProperties props) {
        Object retryCount = props.getHeader("x-retry-count");
        if (retryCount instanceof Integer i) return i;
        if (retryCount instanceof Long l) return l.intValue();
        return 0;
    }

    private void incrementRetryAndSendToDlq(Message message, int currentRetry) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DLQ_EXCHANGE,
                RabbitMQConfig.QUEUE_ORDER_PROCESS_DLQ,
                message.getBody(),
                msg -> {
                    msg.getMessageProperties()
                            .getHeaders()
                            .putAll(message.getMessageProperties().getHeaders());
                    msg.getMessageProperties()
                            .setHeader("x-retry-count", currentRetry + 1);
                    return msg;
                }
        );
        log.info("Mensagem enviada para DLQ com retry-count={}", currentRetry + 1);
    }

    private void sendToParkingLot(Message message, int retryCount) {
        rabbitTemplate.convertAndSend(
                "",
                RabbitMQConfig.QUEUE_ORDER_PROCESS_PARKING_LOT,
                message.getBody(),
                msg -> {
                    msg.getMessageProperties()
                            .getHeaders()
                            .putAll(message.getMessageProperties().getHeaders());
                    msg.getMessageProperties()
                            .setHeader("x-retry-count", retryCount);
                    return msg;
                }
        );
        log.info("Mensagem enviada para parking lot após {} tentativas", retryCount);
    }
}