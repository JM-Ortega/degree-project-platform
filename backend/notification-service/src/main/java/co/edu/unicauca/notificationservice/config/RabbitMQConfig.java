package co.edu.unicauca.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.queues.notification}")
    private String notificationQueue;

    @Value("${messaging.routing.notificationAny}")
    private String notificationRoutingKeyPattern;

    // Exchange principal (topic)
    @Bean
    public TopicExchange degreeExchange() {
        return new TopicExchange(mainExchange, true, false);
    }

    // Cola de notificaciones
    @Bean
    public Queue notificationQueue() {
        return new Queue(notificationQueue, true);
    }

    // Binding entre exchange y cola
    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange degreeExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(degreeExchange)
                .with(notificationRoutingKeyPattern);
    }

    // Administración de la infraestructura AMQP
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // RabbitTemplate para publicar (en caso de que lo necesites en el futuro)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}




