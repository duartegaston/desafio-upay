package com.example.api.service;

import com.example.api.model.AuditLog;
import com.example.api.repository.AuditLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public Page<AuditLog> search(
            Integer page,
            Integer size,
            String method,
            String pathContains,
            String username,
            String status,
            Instant from,
            Instant to
    ) {
        Pageable pageable = PageRequest.of(
                page != null && page >= 0 ? page : 0,
                size != null && size > 0 ? size : 10,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<AuditLog> spec = buildSpecification(method, pathContains, username, status, from, to);

        return auditLogRepository.findAll(spec, pageable);
    }

    private Specification<AuditLog> buildSpecification(
            String method, String pathContains, String username, String status,
            Instant from, Instant to
    ) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            if (method != null && !method.isBlank()) {
                preds.add(cb.equal(cb.upper(root.get("method")), method.toUpperCase()));
            }
            if (pathContains != null && !pathContains.isBlank()) {
                preds.add(cb.like(cb.lower(root.get("path")), "%" + pathContains.toLowerCase() + "%"));
            }
            if (username != null && !username.isBlank()) {
                preds.add(cb.equal(root.get("username"), username));
            }
            if (status != null && !status.isBlank()) {
                preds.add(cb.equal(root.get("status"), status));
            }
            if (from != null) {
                preds.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (to != null) {
                preds.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }
}

