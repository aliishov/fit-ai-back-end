package org.raul.fit_ai.notification;

import org.raul.fit_ai.common.exception.NotificationException;
import org.raul.fit_ai.notification.client.DeviceTokenClient;
import org.raul.fit_ai.notification.dto.ResolvedNotificationPayload;
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;
import org.raul.fit_ai.notification.service.FcmPushNotificationSender;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("FcmPushNotificationSender Unit Tests")
class FcmPushNotificationSenderTest {

	private FcmPushNotificationSender fcmSender;

	@Mock
	private FirebaseMessaging firebaseMessaging;

	@Mock
	private DeviceTokenClient deviceTokenClient;

	@Mock
	private BatchResponse batchResponse;

	@Mock
	private SendResponse successResponse;

	@Mock
	private SendResponse failureResponse;

	private UUID userId;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		fcmSender = new FcmPushNotificationSender(firebaseMessaging, deviceTokenClient);
		userId = UUID.randomUUID();
	}

	@Test
	@DisplayName("Should return PUSH channel")
	void shouldReturnPushChannel() {
		assertEquals(NotificationChannel.PUSH, fcmSender.channel());
	}

	@Test
	@DisplayName("Should send push notification successfully with device tokens from client")
	void sendPushNotificationSuccessfully() throws FirebaseMessagingException {
		List<String> deviceTokens = List.of("token1", "token2");
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.PUSH,
				NotificationType.REMINDER,
				null,
				null,
				"Workout Time",
				"Time for your scheduled workout"
		);

		when(deviceTokenClient.findActiveTokensByUserId(userId)).thenReturn(deviceTokens);
		when(firebaseMessaging.sendEachForMulticast(any())).thenReturn(batchResponse);
		when(batchResponse.getResponses()).thenReturn(List.of(successResponse, successResponse));
		when(batchResponse.getSuccessCount()).thenReturn(2);
		when(batchResponse.getFailureCount()).thenReturn(0);
		when(successResponse.isSuccessful()).thenReturn(true);

		fcmSender.send(payload);

		verify(firebaseMessaging).sendEachForMulticast(any());
	}

	@Test
	@DisplayName("Should use provided device token instead of fetching from client")
	void sendPushWithProvidedDeviceToken() throws FirebaseMessagingException {
		String deviceToken = "specific-device-token";
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.PUSH,
				NotificationType.REMINDER,
				deviceToken,
				null,
				"Title",
				"Body"
		);

		when(firebaseMessaging.sendEachForMulticast(any())).thenReturn(batchResponse);
		when(batchResponse.getResponses()).thenReturn(List.of(successResponse));
		when(batchResponse.getSuccessCount()).thenReturn(1);
		when(batchResponse.getFailureCount()).thenReturn(0);
		when(successResponse.isSuccessful()).thenReturn(true);

		fcmSender.send(payload);

		verify(deviceTokenClient, never()).findActiveTokensByUserId(userId);
		verify(firebaseMessaging).sendEachForMulticast(any());
	}

	@Test
	@DisplayName("Should not send if no active device tokens available")
	void shouldNotSendIfNoDeviceTokens() throws FirebaseMessagingException {
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.PUSH,
				NotificationType.REMINDER,
				null,
				null,
				"Title",
				"Body"
		);

		when(deviceTokenClient.findActiveTokensByUserId(userId)).thenReturn(List.of());

		fcmSender.send(payload);

		verify(firebaseMessaging, never()).sendEachForMulticast(any());
	}

	@Test
	@DisplayName("Should deactivate expired device tokens")
	void shouldDeactivateExpiredDeviceTokens() throws FirebaseMessagingException {
		String expiredToken = "expired-token";
		String validToken = "valid-token";
		List<String> deviceTokens = List.of(expiredToken, validToken);

		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.PUSH,
				NotificationType.REMINDER,
				null,
				null,
				"Title",
				"Body"
		);

		when(deviceTokenClient.findActiveTokensByUserId(userId)).thenReturn(deviceTokens);

		FirebaseMessagingException unregisteredError = mock(FirebaseMessagingException.class);
		when(unregisteredError.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);
		when(failureResponse.isSuccessful()).thenReturn(false);
		when(failureResponse.getException()).thenReturn(unregisteredError);
		when(successResponse.isSuccessful()).thenReturn(true);

		when(firebaseMessaging.sendEachForMulticast(any())).thenReturn(batchResponse);
		when(batchResponse.getResponses()).thenReturn(List.of(failureResponse, successResponse));
		when(batchResponse.getSuccessCount()).thenReturn(1);
		when(batchResponse.getFailureCount()).thenReturn(1);

		fcmSender.send(payload);

		verify(deviceTokenClient).deactivateByToken(expiredToken);
	}

	@Test
	@DisplayName("Should throw NotificationException on Firebase error")
	void throwNotificationExceptionOnFirebaseError() throws FirebaseMessagingException {
		List<String> deviceTokens = List.of("token1");
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.PUSH,
				NotificationType.REMINDER,
				null,
				null,
				"Title",
				"Body"
		);

		when(deviceTokenClient.findActiveTokensByUserId(userId)).thenReturn(deviceTokens);
		FirebaseMessagingException firebaseError = mock(FirebaseMessagingException.class);
		when(firebaseMessaging.sendEachForMulticast(any()))
				.thenThrow(firebaseError);

		assertThrows(NotificationException.class, () -> fcmSender.send(payload));
	}

	@Test
	@DisplayName("Should handle multiple device tokens")
	void handleMultipleDeviceTokens() throws FirebaseMessagingException {
		List<String> deviceTokens = List.of("token1", "token2", "token3");
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.PUSH,
				NotificationType.REMINDER,
				null,
				null,
				"Title",
				"Body"
		);

		when(deviceTokenClient.findActiveTokensByUserId(userId)).thenReturn(deviceTokens);
		when(firebaseMessaging.sendEachForMulticast(any())).thenReturn(batchResponse);
		when(batchResponse.getResponses()).thenReturn(
				List.of(successResponse, successResponse, successResponse)
		);
		when(batchResponse.getSuccessCount()).thenReturn(3);
		when(batchResponse.getFailureCount()).thenReturn(0);
		when(successResponse.isSuccessful()).thenReturn(true);

		fcmSender.send(payload);

		verify(firebaseMessaging).sendEachForMulticast(any());
	}

	@Test
	@DisplayName("Should handle partial failures in batch send")
	void handlePartialFailuresInBatchSend() throws FirebaseMessagingException {
		List<String> deviceTokens = List.of("token1", "token2", "token3");
		ResolvedNotificationPayload payload = new ResolvedNotificationPayload(
				userId,
				NotificationChannel.PUSH,
				NotificationType.REMINDER,
				null,
				null,
				"Title",
				"Body"
		);

		when(deviceTokenClient.findActiveTokensByUserId(userId)).thenReturn(deviceTokens);

		FirebaseMessagingException otherError = mock(FirebaseMessagingException.class);
		when(otherError.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNAVAILABLE);
		when(failureResponse.isSuccessful()).thenReturn(false);
		when(failureResponse.getException()).thenReturn(otherError);
		when(successResponse.isSuccessful()).thenReturn(true);

		when(firebaseMessaging.sendEachForMulticast(any())).thenReturn(batchResponse);
		when(batchResponse.getResponses()).thenReturn(
				List.of(successResponse, failureResponse, successResponse)
		);
		when(batchResponse.getSuccessCount()).thenReturn(2);
		when(batchResponse.getFailureCount()).thenReturn(1);

		fcmSender.send(payload);

		verify(firebaseMessaging).sendEachForMulticast(any());
	}
}
