package br.com.cleanprosolutions.user.event.consumer;

import br.com.cleanprosolutions.user.event.dto.RatingCreatedEvent;
import br.com.cleanprosolutions.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer for {@code RatingCreated} events published by the {@code rating-service}.
 *
 * <p>Updates the contractor's aggregated rating upon receiving the event.
 * Implements idempotency by relying on the service layer's silent-ignore policy
 * when the contractor ID is not found.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RatingCreatedConsumer {

    private final UserService userService;

    /**
     * Handles the {@code RatingCreated} event from RabbitMQ.
     *
     * @param event the rating event payload
     */
    @RabbitListener(queues = "${rabbitmq.queue.rating-created:user-service.rating-created}")
    public void handle(final RatingCreatedEvent event) {
        log.info("Received RatingCreated event — eventId: {}, contractorId: {}, avgRating: {}",
                event.eventId(), event.contractorId(), event.avgRating());

        userService.updateRating(event.contractorId(), event.avgRating(), event.totalRatings());

        log.info("RatingCreated event processed — eventId: {}", event.eventId());
    }
}
