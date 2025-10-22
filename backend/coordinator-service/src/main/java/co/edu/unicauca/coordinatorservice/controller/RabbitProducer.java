package co.edu.unicauca.coordinatorservice.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Envía un mensaje a un routing key específico.
     */
    public void sendMessage(String routingKey, Object message) {
        rabbitTemplate.convertAndSend(mainExchange, routingKey, message);
        System.out.println("Mensaje enviado → Exchange: " + mainExchange + " | Routing key: " + routingKey);
    }
}
