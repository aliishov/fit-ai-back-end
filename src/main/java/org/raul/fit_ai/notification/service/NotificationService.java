package org.raul.fit_ai.notification.service;

import org.raul.fit_ai.common.exception.NotificationException;
import org.raul.fit_ai.notification.dto.NotificationPayload;
import org.raul.fit_ai.notification.dto.ResolvedNotificationPayload;
import org.raul.fit_ai.notification.model.NotificationLog;
import org.raul.fit_ai.notification.model.NotificationTemplate;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationStatus;
import org.raul.fit_ai.notification.repository.NotificationLogRepository;
import org.raul.fit_ai.notification.repository.NotificationTemplateRepository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

	Map<NotificationChannel, NotificationSender> senders;
	NotificationTemplateRepository templateRepository;
	NotificationLogRepository logRepository;

	public NotificationService(List<NotificationSender> senderList,
	                           NotificationTemplateRepository templateRepository,
	                           NotificationLogRepository logRepository) {
		this.senders = senderList.stream()
				.collect(Collectors.toMap(NotificationSender::channel, s -> s));
		this.templateRepository = templateRepository;
		this.logRepository = logRepository;
	}

	public void send(NotificationPayload payload) {
		NotificationTemplate template = templateRepository
				.findByTypeAndChannelAndActive(payload.type(), payload.channel(), true)
				.orElseThrow(() -> new TemplateNotFoundException(
						"Template not found for type=%s channel=%s"
								.formatted(payload.type(), payload.channel())));

		String resolvedBody = interpolate(template.getBody(), payload.variables());

		ResolvedNotificationPayload resolved = new ResolvedNotificationPayload(
				payload.userId(),
				payload.channel(),
				payload.type(),
				payload.recipient(),
				template.getSubject(),
				payload.title(),
				resolvedBody
		);


		NotificationSender sender = senders.get(payload.channel());
		if (sender == null) {
			throw new NotificationException("No sender for channel: " + payload.channel());
		}

		NotificationStatus status = NotificationStatus.SENT;
		String error = null;

		try {
			sender.send(resolved);
		} catch (Exception e) {
			status = NotificationStatus.FAILED;
			error = e.getMessage();
			throw e;
		} finally {
			saveLog(resolved, status, error);
		}
	}

	private String interpolate(String template, Map<String, String> variables) {
		if (template == null || variables == null || variables.isEmpty()) return template;
		String result = template;
		for (Map.Entry<String, String> entry : variables.entrySet()) {
			result = result.replace("{" + entry.getKey() + "}", entry.getValue());
		}
		return result;
	}

	private void saveLog(ResolvedNotificationPayload payload,
	                     NotificationStatus status, String error) {
		logRepository.save(NotificationLog.builder()
				.userId(payload.userId())
				.channel(payload.channel())
				.type(payload.type())
				.recipient(payload.recipient())
				.status(status)
				.errorMessage(error)
				.build());
	}
}
