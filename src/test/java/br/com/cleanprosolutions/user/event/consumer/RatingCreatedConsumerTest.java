package br.com.cleanprosolutions.user.event.consumer;

import br.com.cleanprosolutions.user.event.dto.RatingCreatedEvent;
import br.com.cleanprosolutions.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link RatingCreatedConsumer}.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RatingCreatedConsumerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RatingCreatedConsumer consumer;

    @Test
    @DisplayName("shouldDelegateRatingUpdateToUserServiceWhenEventReceived")
    void shouldDelegateRatingUpdateToUserServiceWhenEventReceived() {
        final RatingCreatedEvent event = new RatingCreatedEvent(
                "event-id-001", "contractor-id-001", 4.8, 25, Instant.now());

        consumer.handle(event);

        verify(userService).updateRating("contractor-id-001", 4.8, 25);
    }
}
