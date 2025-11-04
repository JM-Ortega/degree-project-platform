package co.edu.unicauca.academicprojectservice.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
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
// ... imports y @Configuration como ya lo tienes

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

    // --- NUEVO ---
    @Value("${messaging.queues.projectFormatoA}")
    private String projectFormatoAQueue;

    @Value("${messaging.routing.projectCreated}")
    private String projectCreatedRoutingKey;

    @Value("${messaging.routing.projectUpdated}")
    private String projectUpdatedRoutingKey;

    @Value("${messaging.routing.userCreated}")
    private String userCreatedRoutingKey;

    @Value("${messaging.routing.formatAApprovedByCoordinator}")
    private String formatAApprovedByCoordinatorRoutingKey;

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
    // 3. Declaración de colas (principal, DLQ y la nueva de FormatoA)
    // =====================================================
    @Bean
    public Queue projectQueue() {
        return QueueBuilder.durable(projectQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", projectDlq)
                .build();
    }

    @Bean
    public Queue projectDlq() {
        return QueueBuilder.durable(projectDlq).build();
    }

    // --- NUEVO: cola dedicada a eventos de FormatoA ---
    @Bean
    public Queue projectFormatoAQueue() {
        return QueueBuilder.durable(projectFormatoAQueue)
                // si quieres una DLQ propia, crea otra cola y cámbiala aquí;
                // por ahora reutilizamos la misma DLQ 'projectDlq'
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", projectDlq)
                .build();
    }

    // =====================================================
    // 4. Bindings
    // =====================================================
    @Bean
    public Binding bindingProjectCreated(Queue projectQueue, TopicExchange mainExchange) {
        return BindingBuilder.bind(projectQueue).to(mainExchange).with(projectCreatedRoutingKey);
    }

    @Bean
    public Binding bindingProjectUpdated(Queue projectQueue, TopicExchange mainExchange) {
        return BindingBuilder.bind(projectQueue).to(mainExchange).with(projectUpdatedRoutingKey);
    }

    // --- IMPORTANTE: mueve formatA.approved a la cola nueva ---
    @Bean
    public Binding bindingFormatoAApproved(Queue projectFormatoAQueue, TopicExchange mainExchange) {
        return BindingBuilder.bind(projectFormatoAQueue).to(mainExchange).with(formatAApprovedByCoordinatorRoutingKey);
    }

    // =====================================================
    // 4.1 Bindings DLX -> DLQ
    // =====================================================

    @Bean
    public Binding bindProjectDlqToDlx(
            @Qualifier("projectDlq") Queue projectDlqQueue,
            TopicExchange deadLetterExchange,
            @Value("${messaging.queues.projectDlq}") String rkToDlq) {

        // routing key = nombre de la DLQ (debe coincidir con x-dead-letter-routing-key)
        return BindingBuilder
                .bind(projectDlqQueue)
                .to(deadLetterExchange)
                .with(rkToDlq);
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
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }
}
