package com.equipo31.app.repository;

import com.equipo31.app.entity.SentimentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentimentRecordRepository extends JpaRepository<SentimentRecord, Long> {
    long countByPrevision(String prevision);
}
