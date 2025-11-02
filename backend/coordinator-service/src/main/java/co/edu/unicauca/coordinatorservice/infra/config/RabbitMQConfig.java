package co.edu.unicauca.coordinatorservice.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.exchange.dlx}")
    private String dlxExchange;

    @Value("${messaging.queues.coordinator}")
    private String coordinatorQueue;

    @Value("${messaging.queues.coordinatorDlq}")
    private String coordinatorDlq;

    @Value("${messaging.routing.formatAApprovedByCoordinator}")
    private String routingKeyFormatAApproved;


    // =====================================================
    //  1. Declaramos el exchange principal
    // =====================================================
    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(mainExchange);
    }

    //  2. Declaramos el exchange Dead Letter (DLX)
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(dlxExchange);
    }

    // =====================================================
    //  3. Declaramos la cola principal del coordinador
    // =====================================================
    @Bean
    public Queue coordinatorQueue() {
        return QueueBuilder
                .durable(coordinatorQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange) // si falla, se envía al DLX
                .withArgument("x-dead-letter-routing-key", coordinatorDlq)
                .build();
    }

    //  4. Declaramos la cola DLQ del coordinador
    @Bean
    public Queue coordinatorDlq() {
        return QueueBuilder.durable(coordinatorDlq).build();
    }

    // =====================================================
    //  5. Enlazamos la cola con el exchange
    // =====================================================
    @Bean
    public Binding bindingCoordinator() {
        return BindingBuilder
                .bind(coordinatorQueue())
                .to(mainExchange())
                .with(routingKeyFormatAApproved);
    }

    // =====================================================
    //  6. Convertidor JSON (Jackson)
    // =====================================================
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //  7. RabbitTemplate con convertidor JSON
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}

