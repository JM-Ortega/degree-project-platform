package co.edu.unicauca.coordinatorservice.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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

    @Value("${messaging.queues.coordinatorAuth}")
    private String coordinatorAuthQueue; // ✅ nueva cola para eventos de usuario

    @Value("${messaging.routing.formatAApprovedByCoordinator}")
    private String routingKeyFormatAApproved;

    // 1) Exchange principal
    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(mainExchange, true, false);
    }

    // 2) Dead Letter Exchange (DLX)
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(dlxExchange, true, false);
    }

    // 3) Cola principal
    @Bean
    public Queue coordinatorQueueBean() {
        return QueueBuilder.durable(coordinatorQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", coordinatorDlq)
                .build();
    }

    // 4) Cola de mensajes muertos (DLQ)
    @Bean
    public Queue coordinatorDlqBean() {
        return QueueBuilder.durable(coordinatorDlq).build();
    }

    // 5) Binding: mainExchange -> coordinatorQueue
    @Bean
    public Binding bindingCoordinator() {
        return BindingBuilder
                .bind(coordinatorQueueBean())
                .to(mainExchange())
                .with(routingKeyFormatAApproved);
    }

    // ✅ NUEVA COLA para eventos de creación de usuario (auth.user.created)
    @Bean
    public Queue coordinatorAuthQueueBean() {
        return QueueBuilder.durable(coordinatorAuthQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", coordinatorDlq)
                .build();
    }

    // ✅ Binding para la cola de usuarios
    @Bean
    public Binding bindCoordinatorAuthUserCreated() {
        return BindingBuilder.bind(coordinatorAuthQueueBean())
                .to(mainExchange())
                .with("auth.user.created");
    }

    // 6) Binding: DLX -> DLQ
    @Bean
    public Binding bindingCoordinatorDlq() {
        return BindingBuilder
                .bind(coordinatorDlqBean())
                .to(deadLetterExchange())
                .with(coordinatorDlq);
    }

    // 7) Convertidor JSON
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 8) RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    // 9) Listener Factory
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
