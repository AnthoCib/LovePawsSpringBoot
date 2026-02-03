package com.lovepaws.app.user.repository;

import com.lovepaws.app.user.domain.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
}
