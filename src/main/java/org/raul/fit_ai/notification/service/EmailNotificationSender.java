package org.raul.fit_ai.notification.service;

import org.raul.fit_ai.common.exception.NotificationException;
import org.raul.fit_ai.notification.dto.ResolvedNotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EmailNotificationSender implements NotificationSender {

	final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	String from;

	@Override
	public void send(ResolvedNotificationPayload payload) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(from);
			helper.setTo(payload.recipient());
			helper.setSubject(payload.subject() != null ? payload.subject() : "");
			helper.setText(payload.body(), true);

			mailSender.send(message);
			log.info("Email sent to [{}] type=[{}]", payload.recipient(), payload.type());
		} catch (MessagingException e) {
			log.error("Failed to send email to [{}]", payload.recipient(), e);
			throw new NotificationException("Failed to send email", e);
		}
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}
}
