package org.raul.fit_ai.notification.repository;

import org.raul.fit_ai.notification.model.NotificationLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}
