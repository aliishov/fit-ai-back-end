package org.raul.fit_ai.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponseDTO<T> {

	boolean success;
	String message;
	T data;
	OffsetDateTime timestamp;
	Map<String, List<String>> errors;

	// Success with data + message
	public static <T> BaseResponseDTO<T> success(T data, String message) {
		return BaseResponseDTO.<T>builder()
				.success(true)
				.message(message)
				.data(data)
				.timestamp(OffsetDateTime.now())
				.build();
	}

	// Success with data only
	public static <T> BaseResponseDTO<T> success(T data) {
		return success(data, null);
	}

	public static BaseResponseDTO<Void> success(String message) {
		return BaseResponseDTO.<Void>builder()
				.success(true)
				.message(message)
				.timestamp(OffsetDateTime.now())
				.build();
	}

	// Success with message only
	public static BaseResponseDTO<Void> success() {
		return BaseResponseDTO.<Void>builder()
				.success(true)
				.timestamp(OffsetDateTime.now())
				.build();
	}

	// Error with field-level validation errors
	public static <T> BaseResponseDTO<T> error(String message, Map<String, List<String>> errors) {
		return BaseResponseDTO.<T>builder()
				.success(false)
				.message(message)
				.errors(errors)
				.timestamp(OffsetDateTime.now())
				.build();
	}

	// Error message only
	public static <T> BaseResponseDTO<T> error(String message) {
		return error(message, null);
	}
}
