package co.edu.unicauca.authservice.infra.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración central de RabbitMQ para el microservicio de autenticación (Auth).
 *
 * <p>
 * Define las colas, intercambios y bindings necesarios para que el microservicio
 * pueda publicar y/o escuchar eventos a través del <b>exchange principal</b> y su
 * correspondiente <b>Dead Letter Exchange (DLX)</b>.
 * </p>
 *
 * <p>
 * Los nombres de colas, exchanges y rutas se obtienen desde el archivo
 * <code>application.yml</code> (bloque <b>messaging.*</b>), de modo que
 * la topología puede modificarse fácilmente por entorno sin tocar el código.
 * </p>
 *
 * <p>
 * Este micro normalmente <b>publica</b> eventos como:
 * <ul>
 *   <li><code>auth.user.created</code> → notifica creación de usuario</li>
 *   <li><code>notification.send</code> → solicita envío de correo</li>
 * </ul>
 * </p>
 *
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

    @Value("${messaging.queues.auth}")
    private String authQueueName;

    @Value("${messaging.queues.authDlq}")
    private String authDlqName;

    // ======================================================
    //  EXCHANGES
    // ======================================================

    /**
     * Exchange principal donde se publican los eventos del dominio.
     */
    @Bean
    public TopicExchange mainExchange() {
        return ExchangeBuilder
                .topicExchange(mainExchangeName)
                .durable(true)
                .build();
    }

    /**
     * Dead Letter Exchange: recibe los mensajes que no pudieron ser procesados.
     */
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

    /**
     * Cola principal del microservicio Auth.
     * <p>
     * Incluye referencia al DLX para manejo de errores.
     * </p>
     */
    @Bean
    public Queue authQueue() {
        return QueueBuilder
                .durable(authQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", authDlqName)
                .build();
    }

    /**
     * Dead Letter Queue para el microservicio Auth.
     * <p>
     * Guarda los mensajes fallidos en la cola principal.
     * </p>
     */
    @Bean
    public Queue authDlq() {
        return QueueBuilder
                .durable(authDlqName)
                .build();
    }

    // ======================================================
    //  BINDINGS
    // ======================================================

    /**
     * Binding entre el exchange principal y la cola del micro Auth.
     * <p>
     * Enlaza todos los mensajes que comiencen con <code>auth.*</code>,
     * permitiendo al micro escuchar sus propios eventos si fuera necesario.
     * </p>
     */
    @Bean
    public Binding authBinding() {
        return BindingBuilder
                .bind(authQueue())
                .to(mainExchange())
                .with("auth.#");
    }

    /**
     * Binding de la Dead Letter Queue al Dead Letter Exchange.
     */
    @Bean
    public Binding authDlqBinding() {
        return BindingBuilder
                .bind(authDlq())
                .to(dlxExchange())
                .with(authDlqName);
    }
    // ======================================================
    //  CONVERTER + RABBIT TEMPLATE
    // ======================================================

    /**
     * Converter que le dice a Spring AMQP:
     * "todo lo que me manden en convertAndSend lo serializo/deserializo como JSON".
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(com.fasterxml.jackson.databind.ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }


    /**
     * RabbitTemplate que usa el converter anterior.
     * Este es el que deben inyectar tus publishers:
     * - UserEventsPublisher
     * - NotificationPublisher
     */
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