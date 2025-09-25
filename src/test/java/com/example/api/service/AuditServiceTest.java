package com.example.api.service;

import com.example.api.model.AuditLog;
import com.example.api.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuditServiceTest {

    private AuditLogRepository repository;
    private AuditService service;

    @BeforeEach
    void setUp() {
        repository = mock(AuditLogRepository.class);
        service = new AuditService(repository);
    }

    @Test
    void search_invokesRepositoryWithSpecAndPageable() {
        service.search(0, 10, "POST", "/auth", "userNameTest", "200", null, null);

        ArgumentCaptor<Specification<AuditLog>> specCap = ArgumentCaptor.forClass((Class) Specification.class);
        ArgumentCaptor<Pageable> pageCap = ArgumentCaptor.forClass(Pageable.class);

        verify(repository, times(1)).findAll(specCap.capture(), pageCap.capture());
        assertNotNull(specCap.getValue());
        Pageable p = pageCap.getValue();
        assertEquals(0, p.getPageNumber());
        assertEquals(10, p.getPageSize());
    }
}
