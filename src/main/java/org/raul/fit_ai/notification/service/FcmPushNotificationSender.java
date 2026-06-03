package org.raul.fit_ai.notification.service;

import org.raul.fit_ai.common.exception.NotificationException;
import org.raul.fit_ai.notification.client.DeviceTokenClient;
import org.raul.fit_ai.notification.dto.ResolvedNotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FcmPushNotificationSender implements NotificationSender {

	FirebaseMessaging firebaseMessaging;
	DeviceTokenClient deviceTokenClient;


	@Override
	public void send(ResolvedNotificationPayload payload) {
		log.info("Sending push to user=[{}] type=[{}]", payload.userId(), payload.type());

		List<String> tokens = resolveTokens(payload);

		if (tokens.isEmpty()) {
			log.warn("No active device tokens for user=[{}]", payload.userId());
			return;
		}

		MulticastMessage message = MulticastMessage.builder()
				.setNotification(Notification.builder()
						.setTitle(payload.title())
						.setBody(payload.body())
						.build())
				.addAllTokens(tokens)
				.build();

		try {
			BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
			handleResponse(response, tokens, payload.userId());
		} catch (FirebaseMessagingException e) {
			log.error("Failed to send push to user=[{}]", payload.userId(), e);
			throw new NotificationException("Failed to send push notification", e);
		}
	}

	private void handleResponse(BatchResponse response,
	                            List<String> tokens, UUID userId) {
		List<SendResponse> responses = response.getResponses();

		for (int i = 0; i < responses.size(); i++) {
			SendResponse sendResponse = responses.get(i);
			if (!sendResponse.isSuccessful()) {
				FirebaseMessagingException ex = sendResponse.getException();
				if (ex != null && ex.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
					log.warn("Device token expired for user=[{}] — deactivating", userId);
					deviceTokenClient.deactivateByToken(tokens.get(i));
				}
			}
		}

		log.info("Push sent to user=[{}] success=[{}] failure=[{}]",
				userId, response.getSuccessCount(), response.getFailureCount());
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.PUSH;
	}

	private List<String> resolveTokens(ResolvedNotificationPayload payload) {
		if (payload.recipient() != null && !payload.recipient().isBlank()) {
			return List.of(payload.recipient());
		}

		return deviceTokenClient.findActiveTokensByUserId(payload.userId());
	}
}
