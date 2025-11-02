package co.edu.unicauca.departmentheadservice.infra.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio de Departamento.
 * Configura el Exchange, Queues, y Bindings necesarios para publicar y escuchar eventos.
 */
@Configuration
public class RabbitConfig {

    // ======================================================
    //  Exchanges (cargados desde application.yml)
    // ======================================================
    @Value("${messaging.exchange.main}")
    private String mainExchangeName;

    @Value("${messaging.exchange.dlx}")
    private String dlxExchangeName;

    // ======================================================
    //  Queues (colas principales y DLQ)
    // ======================================================
    @Value("${messaging.queues.department}")
    private String departmentQueueName;

    @Value("${messaging.queues.departmentDlq}")
    private String departmentDlqName;

    // ======================================================
    //  EXCHANGES
    // ======================================================

    @Bean
    public TopicExchange mainExchange() {
        return ExchangeBuilder
                .topicExchange(mainExchangeName)
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange dlxExchange() {
        return ExchangeBuilder
                .topicExchange(dlxExchangeName)
                .durable(true)
                .build();
    }

    // ======================================================
    //  QUEUES
    // ======================================================

    @Bean
    public Queue departmentQueue() {
        return QueueBuilder
                .durable(departmentQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", departmentDlqName)
                .build();
    }

    @Bean
    public Queue departmentDlq() {
        return QueueBuilder
                .durable(departmentDlqName)
                .build();
    }

    // ======================================================
    //  BINDINGS
    // ======================================================

    // Binding para escuchar el evento de creación de usuarios (docentes)
    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder
                .bind(departmentQueue())
                .to(mainExchange())
                .with("auth.user.created"); // Escuchar el evento de usuario creado
    }

    // Binding para escuchar el evento de creación de anteproyectos
    @Bean
    public Binding anteproyectoCreatedBinding() {
        return BindingBuilder
                .bind(departmentQueue())
                .to(mainExchange())
                .with("department.anteproyecto.created"); // Escuchar el evento de anteproyecto creado sin evaluadores
    }

    // Binding para escuchar eventos de Aprobación de Anteproyecto
    @Bean
    public Binding proposalApprovedByDeptHeadBinding() {
        return BindingBuilder
                .bind(departmentQueue())
                .to(mainExchange())
                .with("department.proposal.approved"); // Clave de enrutamiento para Aprobación de Anteproyecto
    }

    // ======================================================
    //  CONVERTER + RABBIT TEMPLATE
    // ======================================================

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(com.fasterxml.jackson.databind.ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}

