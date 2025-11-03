package co.edu.unicauca.academicprojectservice.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Configuración de RabbitMQ para el microservicio Academic Project Service.
 * <p>
 * Define colas, exchange principal, bindings y convertidor de mensajes JSON.
 * Los valores se obtienen desde application.yml.
 * </p>
 */
@Configuration
public class RabbitMQConfig {
    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.queues.project}")
    private String projectQueue;

    @Value("${messaging.routing.userCreated}")
    private String userCreatedRoutingKey;

    @Bean
    public Queue projectQueue() {
        return new Queue(projectQueue, true);
    }

    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(mainExchange);
    }

    @Bean
    public Binding projectBinding(Queue projectQueue, TopicExchange mainExchange) {
        return BindingBuilder.bind(projectQueue).to(mainExchange).with(userCreatedRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}