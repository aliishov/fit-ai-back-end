package org.raul.fit_ai.notification.repository;

import org.raul.fit_ai.notification.model.NotificationTemplate;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

	Optional<NotificationTemplate> findByTypeAndChannelAndActive(NotificationType type, NotificationChannel channel, boolean active);
}
