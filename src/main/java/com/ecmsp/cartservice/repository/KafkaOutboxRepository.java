package com.ecmsp.cartservice.repository;

import com.ecmsp.cartservice.domain.KafkaOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface KafkaOutboxRepository extends JpaRepository<KafkaOutbox, UUID> {

    List<KafkaOutbox> findByProcessedFalseOrderByCreatedAtAsc();

    @Modifying
    @Query("UPDATE KafkaOutbox k SET k.processed = true, k.processedAt = :processedAt WHERE k.id = :id")
    void markAsProcessed(@Param("id") UUID id, @Param("processedAt") LocalDateTime processedAt);

    @Query("SELECT COUNT(k) FROM KafkaOutbox k WHERE k.processed = false")
    long countUnprocessedEvents();

    List<KafkaOutbox> findByProcessedFalseAndCreatedAtBeforeOrderByCreatedAtAsc(LocalDateTime before);
}