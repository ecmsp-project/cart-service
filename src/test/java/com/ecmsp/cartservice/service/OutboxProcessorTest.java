package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.KafkaOutbox;
import com.ecmsp.cartservice.dto.event.ReservationEventPayload;
import com.ecmsp.cartservice.repository.KafkaOutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OutboxProcessorTest {

    @Autowired
    private KafkaOutboxService kafkaOutboxService;

    @Autowired
    private OutboxEventProcessor outboxEventProcessor;

    @Autowired
    private KafkaOutboxRepository kafkaOutboxRepository;

    @BeforeEach
    void setUp() {
        kafkaOutboxRepository.deleteAll();
    }

    @Test
    void shouldProcessOutboxEventAndFillProcessedAt() throws InterruptedException {
        ReservationEventPayload.ProductReservation product = new ReservationEventPayload.ProductReservation(
                123L, UUID.randomUUID(), "Test Product", "Test Description",
                BigDecimal.valueOf(29.99), 1, BigDecimal.valueOf(29.99), true
        );

        ReservationEventPayload eventPayload = ReservationEventPayload.success(
                UUID.randomUUID(), UUID.randomUUID(), List.of(product)
        );

        KafkaOutbox savedEvent = kafkaOutboxService.saveEvent(eventPayload, "TEST_RESERVATION", "test-topic");
        UUID eventId = savedEvent.getId();

        KafkaOutbox unprocessedEvent = kafkaOutboxRepository.findById(eventId).orElseThrow();
        assertThat(unprocessedEvent.isProcessed()).isFalse();
        assertThat(unprocessedEvent.getProcessedAt()).isNull();

        outboxEventProcessor.processOutboxEvents();

        Thread.sleep(1000);

        KafkaOutbox processedEvent = kafkaOutboxRepository.findById(eventId).orElseThrow();
        assertThat(processedEvent.isProcessed()).isTrue();
        assertThat(processedEvent.getProcessedAt()).isNotNull();

        kafkaOutboxRepository.deleteById(eventId);

        boolean exists = kafkaOutboxRepository.existsById(eventId);
        assertThat(exists).isFalse();

        System.out.println("✅ Test passed: Event processed and cleaned up successfully!");
    }
}