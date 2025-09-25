package com.example.api.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuditEventProducer {

    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;

    @Value("${app.audit.topic:audit-events}")
    private String topic;

    public AuditEventProducer(KafkaTemplate<String, AuditEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(AuditEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
