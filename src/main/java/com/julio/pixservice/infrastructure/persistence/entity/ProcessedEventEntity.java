package com.julio.pixservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedEventEntity implements Persistable<String> {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @Override
    public String getId() {
        return eventId;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
