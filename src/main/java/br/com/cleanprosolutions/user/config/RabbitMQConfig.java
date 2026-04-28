package br.com.cleanprosolutions.user.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for the user-service.
 *
 * <p>Declares the exchange and queue for consuming {@code RatingCreated} events
 * published by the {@code rating-service}.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Configuration
public class RabbitMQConfig {

    /** Exchange name for rating events. */
    @Value("${rabbitmq.exchange.rating:rating.exchange}")
    private String ratingExchange;

    /** Queue name for rating-created events in the user-service. */
    @Value("${rabbitmq.queue.rating-created:user-service.rating-created}")
    private String ratingCreatedQueue;

    /** Routing key for rating-created events. */
    @Value("${rabbitmq.routing-key.rating-created:rating.created}")
    private String ratingCreatedRoutingKey;

    /**
     * Declares the topic exchange for rating events.
     *
     * @return the configured {@link TopicExchange}
     */
    @Bean
    public TopicExchange ratingExchange() {
        return new TopicExchange(ratingExchange, true, false);
    }

    /**
     * Declares the durable queue for rating-created events.
     *
     * @return the durable {@link Queue}
     */
    @Bean
    public Queue ratingCreatedQueue() {
        return new Queue(ratingCreatedQueue, true);
    }

    /**
     * Binds the rating-created queue to the rating exchange with the routing key.
     *
     * @return the {@link Binding}
     */
    @Bean
    public Binding ratingCreatedBinding() {
        return BindingBuilder
                .bind(ratingCreatedQueue())
                .to(ratingExchange())
                .with(ratingCreatedRoutingKey);
    }

    /**
     * Configures JSON message conversion for RabbitMQ messages.
     *
     * @return the {@link MessageConverter}
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configures the RabbitTemplate to use JSON message converter.
     *
     * @param connectionFactory the AMQP connection factory
     * @return the configured {@link RabbitTemplate}
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
