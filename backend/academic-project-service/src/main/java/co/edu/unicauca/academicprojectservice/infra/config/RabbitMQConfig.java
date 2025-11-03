package co.edu.unicauca.academicprojectservice.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio Academic Project Service.
 * <p>
 * Define colas, exchange principal, bindings y convertidor de mensajes JSON.
 * Los valores se obtienen desde application.yml.
 * </p>
 */
@Configuration
public class RabbitMQConfig {

    // =====================================================
    // 1. Nombres definidos en application.yml
    // =====================================================
    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.exchange.dlx}")
    private String dlxExchange;

    @Value("${messaging.queues.project}")
    private String projectQueue;

    @Value("${messaging.queues.projectDlq}")
    private String projectDlq;

    @Value("${messaging.routing.projectCreated}")
    private String projectCreatedRoutingKey;

    @Value("${messaging.routing.projectUpdated}")
    private String projectUpdatedRoutingKey;


    // =====================================================
    // 2. Exchange principal y DLX (Dead Letter Exchange)
    // =====================================================
    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(mainExchange);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(dlxExchange);
    }


    // =====================================================
    // 3. Declaración de colas (principal y DLQ)
    // =====================================================
    @Bean
    public Queue projectQueue() {
        return QueueBuilder
                .durable(projectQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange) // si falla, se envía al DLX
                .withArgument("x-dead-letter-routing-key", projectDlq)
                .build();
    }

    @Bean
    public Queue projectDlq() {
        return QueueBuilder.durable(projectDlq).build();
    }


    // =====================================================
    // 4. Bindings: conectamos la cola con el exchange
    // =====================================================
    // Enlazamos la cola con los eventos de creación de proyectos
    @Bean
    public Binding bindingProjectCreated() {
        return BindingBuilder
                .bind(projectQueue())
                .to(mainExchange())
                .with(projectCreatedRoutingKey);
    }

    // Enlazamos la cola con los eventos de actualización de proyectos
    @Bean
    public Binding bindingProjectUpdated() {
        return BindingBuilder
                .bind(projectQueue())
                .to(mainExchange())
                .with(projectUpdatedRoutingKey);
    }


    // =====================================================
    // 5. Convertidor JSON (Jackson)
    // =====================================================
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    // =====================================================
    // 6. RabbitTemplate con convertidor JSON
    // =====================================================
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
