package com.example.api.kafka;

import com.example.api.model.AuditLog;
import com.example.api.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("consumer")
public class AuditEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditEventConsumer.class);

    private final AuditLogRepository repository;

    public AuditEventConsumer(AuditLogRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "${app.audit.topic:audit-events}", groupId = "audit-consumers",
            containerFactory = "auditEventKafkaListenerContainerFactory")
    public void onMessage(AuditEvent event) {
        log.info("[Kafka][AuditEventConsumer] Consumed event: method={}, path={}, user={}, status={}",
                event.getMethod(), event.getPath(), event.getUsername(), event.getStatus());
        AuditLog logEntry = AuditLog.builder()
                .method(event.getMethod())
                .path(event.getPath())
                .username(event.getUsername())
                .status(event.getStatus())
                .build();
        repository.save(logEntry);
    }
}
