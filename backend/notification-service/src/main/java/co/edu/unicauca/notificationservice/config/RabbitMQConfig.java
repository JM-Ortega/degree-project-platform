package co.edu.unicauca.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la infraestructura RabbitMQ utilizada por el servicio de notificaciones.
 * Define los componentes principales: exchange, cola, binding, administración y serialización de mensajes.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.queues.notification}")
    private String notificationQueueName;

    @Value("${messaging.routing.notificationAny}")
    private String notificationRoutingKeyPattern;

    /**
     * Crea el exchange principal de tipo {@link TopicExchange}.
     * Permite la distribución de mensajes según patrones de enrutamiento.
     *
     * @return Exchange configurado.
     */
    @Bean
    public TopicExchange degreeExchange() {
        return new TopicExchange(mainExchange, true, false);
    }

    /**
     * Declara la cola de notificaciones utilizada para recibir eventos.
     *
     * @return Cola duradera para mensajes de notificación.
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(notificationQueueName).build();
    }

    /**
     * Asocia la cola de notificaciones con el exchange mediante un patrón de enrutamiento.
     *
     * @param notificationQueue Cola objetivo.
     * @param degreeExchange    Exchange principal.
     * @return Binding configurado entre la cola y el exchange.
     */
    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange degreeExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(degreeExchange)
                .with(notificationRoutingKeyPattern);
    }

    /**
     * Provee un {@link AmqpAdmin} para la administración programática
     * de colas, exchanges y bindings en RabbitMQ.
     *
     * @param connectionFactory Fábrica de conexiones AMQP.
     * @return Instancia de administración AMQP.
     */
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * Define un convertidor JSON para la serialización y deserialización de mensajes AMQP.
     *
     * @return Convertidor de mensajes basado en Jackson.
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura el {@link RabbitTemplate} utilizado para el envío de mensajes AMQP.
     * Aplica el convertidor JSON definido previamente.
     *
     * @param connectionFactory Fábrica de conexiones AMQP.
     * @param converter         Convertidor de mensajes JSON.
     * @return Plantilla Rabbit configurada.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
