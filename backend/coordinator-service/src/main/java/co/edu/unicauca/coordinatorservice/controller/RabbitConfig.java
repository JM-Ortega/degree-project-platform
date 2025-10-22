package co.edu.unicauca.coordinatorservice.controller;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.queues.coordinator}")
    private String coordinatorQueue;

    @Value("${messaging.queues.coordinatorDlq}")
    private String coordinatorDlq;

    @Bean
    public TopicExchange degreeExchange() {
        return new TopicExchange(mainExchange);
    }

    @Bean
    public Queue coordinatorQueue() {
        return QueueBuilder.durable(coordinatorQueue)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", coordinatorDlq)
                .build();
    }

    @Bean
    public Queue coordinatorDlq() {
        return QueueBuilder.durable(coordinatorDlq).build();
    }

    @Bean
    public Binding bindingCoordinatorQueue(Queue coordinatorQueue, TopicExchange degreeExchange) {
        // Escucha los eventos que te interesen (puedes agregar más)
        return BindingBuilder.bind(coordinatorQueue)
                .to(degreeExchange)
                .with("project.*"); // escuchará project.created y project.updated
    }
}
