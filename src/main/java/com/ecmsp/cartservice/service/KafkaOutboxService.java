package com.ecmsp.cartservice.service;

import com.ecmsp.cartservice.domain.KafkaOutbox;
import com.ecmsp.cartservice.repository.KafkaOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaOutboxService {

    private final KafkaOutboxRepository kafkaOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public KafkaOutbox saveEvent(Object eventPayload, String eventType, String topic) {
        try {
            String payload = objectMapper.writeValueAsString(eventPayload);

            KafkaOutbox outboxEvent = KafkaOutbox.builder()
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .eventType(eventType)
                    .topic(topic)
                    .processed(false)
                    .build();

            KafkaOutbox saved = kafkaOutboxRepository.save(outboxEvent);
            log.debug("Saved outbox event: id={}, eventType={}, topic={}", saved.getId(), eventType, topic);

            return saved;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event payload for eventType: {}", eventType, e);
            throw new RuntimeException("Failed to serialize event payload", e);
        }
    }

    @Transactional(readOnly = true)
    public List<KafkaOutbox> getUnprocessedEvents() {
        return kafkaOutboxRepository.findByProcessedFalseOrderByCreatedAtAsc();
    }

    @Transactional(readOnly = true)
    public List<KafkaOutbox> getUnprocessedEventsOlderThan(LocalDateTime before) {
        return kafkaOutboxRepository.findByProcessedFalseAndCreatedAtBeforeOrderByCreatedAtAsc(before);
    }

    @Transactional
    public void markAsProcessed(UUID eventId) {
        kafkaOutboxRepository.markAsProcessed(eventId, LocalDateTime.now());
        log.debug("Marked outbox event as processed: id={}", eventId);
    }

    @Transactional(readOnly = true)
    public long countUnprocessedEvents() {
        return kafkaOutboxRepository.countUnprocessedEvents();
    }

    @Transactional
    public void deleteProcessedEvents(LocalDateTime before) {
        kafkaOutboxRepository.deleteAll(
            kafkaOutboxRepository.findAll().stream()
                .filter(event -> event.isProcessed() && event.getProcessedAt() != null && event.getProcessedAt().isBefore(before))
                .toList()
        );
        log.info("Deleted processed outbox events older than {}", before);
    }
}