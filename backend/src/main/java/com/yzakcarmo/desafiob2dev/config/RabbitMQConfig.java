package com.yzakcarmo.desafiob2dev.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // Exchange principal — Fanout broadcast para todos os consumers
    public static final String FANOUT_EXCHANGE = "order.events.fanout";

    // Filas principais
    public static final String QUEUE_ORDER_PROCESS      = "order.process";
    public static final String QUEUE_ORDER_NOTIFICATION = "order.notification";

    // Dead Letter Queues
    public static final String QUEUE_ORDER_PROCESS_DLQ  = "order.process.dlq";

    // Parking Lots
    public static final String QUEUE_ORDER_PROCESS_PARKING_LOT = "order.process.parking-lot";

    // Exchange da DLQ — redireciona de volta para a fila principal após TTL
    public static final String DLQ_EXCHANGE = "order.dlq.exchange";

    @Value("${messaging.rabbitmq.dlq-ttl-ms:30000}")
    private long dlqTtlMs;

    @Value("${messaging.rabbitmq.parking-lot-ttl-days:30}")
    private int parkingLotTtlDays;

    // ==========================================
    // EXCHANGE PRINCIPAL (Fanout)
    // ==========================================
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    // ==========================================
    // EXCHANGE DA DLQ — direciona de volta para a fila principal
    // ==========================================
    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE, true, false);
    }

    // ==========================================
    // FILAS PRINCIPAIS
    // ==========================================
    @Bean
    public Queue orderProcessQueue() {
        Map<String, Object> args = new HashMap<>();
        // Mensagens que morrem nesta fila vão para a DLQ
        args.put("x-dead-letter-exchange", DLQ_EXCHANGE);
        args.put("x-dead-letter-routing-key", QUEUE_ORDER_PROCESS_DLQ);
        return new Queue(QUEUE_ORDER_PROCESS, true, false, false, args);
    }

    @Bean
    public Queue orderNotificationQueue() {
        return new Queue(QUEUE_ORDER_NOTIFICATION, true);
    }

    // ==========================================
    // DLQ — mensagens ficam aqui pelo TTL e voltam para a fila principal
    // ==========================================
    @Bean
    public Queue orderProcessDlq() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", dlqTtlMs);
        // Após o TTL, volta para a fila principal via dlqExchange
        args.put("x-dead-letter-exchange", FANOUT_EXCHANGE);
        return new Queue(QUEUE_ORDER_PROCESS_DLQ, true, false, false, args);
    }

    // ==========================================
    // PARKING LOT — mensagens irrecuperáveis ficam aqui
    // ==========================================
    @Bean
    public Queue orderProcessParkingLot() {
        Map<String, Object> args = new HashMap<>();
        long parkingLotTtlMs = (long) parkingLotTtlDays * 24 * 60 * 60 * 1000;
        args.put("x-message-ttl", parkingLotTtlMs);
        return new Queue(QUEUE_ORDER_PROCESS_PARKING_LOT, true, false, false, args);
    }

    // ==========================================
    // BINDINGS — conecta filas ao exchange
    // ==========================================
    @Bean
    public Binding bindProcessQueue() {
        return BindingBuilder.bind(orderProcessQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding bindNotificationQueue() {
        return BindingBuilder.bind(orderNotificationQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding bindDlqToExchange() {
        return BindingBuilder.bind(orderProcessDlq())
                .to(dlqExchange())
                .with(QUEUE_ORDER_PROCESS_DLQ);
    }

    // ==========================================
    // SERIALIZAÇÃO JSON
    // ==========================================
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}