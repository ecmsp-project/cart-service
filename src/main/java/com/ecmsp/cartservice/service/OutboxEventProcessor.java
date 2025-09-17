package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.KafkaOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventProcessor {

    private final KafkaOutboxService kafkaOutboxService;

    @Scheduled(fixedDelay = 10)
    public void processOutboxEvents() {
        try {
            List<KafkaOutbox> unprocessedEvents = kafkaOutboxService.getUnprocessedEvents();

            if (unprocessedEvents.isEmpty()) {
                return;
            }

            log.debug("Processing {} unprocessed outbox events", unprocessedEvents.size());

            for (KafkaOutbox event : unprocessedEvents) {
                processEvent(event);
            }

        } catch (Exception e) {
            log.error("Error processing outbox events", e);
        }
    }

    private void processEvent(KafkaOutbox event) {
        try {
            log.info("Simulating Kafka send - Topic: {}, EventType: {}, EventId: {}, Payload: {}",
                    event.getTopic(),
                    event.getEventType(),
                    event.getId(),
                    event.getPayload());

            kafkaOutboxService.markAsProcessed(event.getId());

            log.info("Successfully processed outbox event: id={}, eventType={}",
                    event.getId(), event.getEventType());

        } catch (Exception e) {
            log.error("Failed to process outbox event: id={}, eventType={}",
                    event.getId(), event.getEventType(), e);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void cleanupProcessedEvents() {
        try {
            long unprocessedCount = kafkaOutboxService.countUnprocessedEvents();
            log.info("Outbox status: {} unprocessed events", unprocessedCount);

        } catch (Exception e) {
            log.error("Error during outbox cleanup", e);
        }
    }
}