package org.raul.fit_ai.common.exception;

import org.raul.fit_ai.common.dto.BaseResponseDTO;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidOtpException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleInvalidOtp(InvalidOtpException ex) {
		return ResponseEntity
				.status(HttpStatus.UNPROCESSABLE_CONTENT)
				.body(BaseResponseDTO.error(ex.getMessage()));
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleInvalidToken(InvalidTokenException ex) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(BaseResponseDTO.error(ex.getMessage()));
	}

	@ExceptionHandler(NotificationException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleNotification(NotificationException ex) {
		return ResponseEntity
				.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(BaseResponseDTO.error("Notification service unavailable - please try again"));
	}

	@ExceptionHandler(TemplateNotFoundException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleTemplateNotFound(TemplateNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(BaseResponseDTO.error(ex.getMessage()));
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleDuplicateResource(DuplicateResourceException ex) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(BaseResponseDTO.error(ex.getMessage()));
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleBadRequest(BadRequestException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(BaseResponseDTO.error(ex.getMessage()));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleEntityNotFound(EntityNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(BaseResponseDTO.error(ex.getMessage()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleUnauthorized(UnauthorizedException ex) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(BaseResponseDTO.error(ex.getMessage()));
	}

	@ExceptionHandler(PasswordsDoNotMatchException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handlePasswordsDoNotMatch(PasswordsDoNotMatchException ex) {
		return ResponseEntity
				.status(HttpStatus.UNPROCESSABLE_CONTENT)
				.body(BaseResponseDTO.error(ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, List<String>> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(Collectors.groupingBy(
						FieldError::getField,
						Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
				));

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(BaseResponseDTO.error("Validation failed", errors));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleConstraintViolation(ConstraintViolationException ex) {
		Map<String, List<String>> errors = ex.getConstraintViolations()
				.stream()
				.collect(Collectors.groupingBy(
						v -> v.getPropertyPath().toString(),
						Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
				));

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(BaseResponseDTO.error("Validation failed", errors));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(BaseResponseDTO.error("Malformed request body"));
	}

	@ExceptionHandler(MethodNotAllowedException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleMethodNotAllowed(MethodNotAllowedException ex) {
		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(BaseResponseDTO.error("Method not allowed"));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleNoResourceFound(NoResourceFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(BaseResponseDTO.error("Resource not found"));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleAccessDenied(AccessDeniedException ex) {
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body(BaseResponseDTO.error("Access denied"));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponseDTO<Void>> handleGeneral(Exception ex) {
		log.error("Unexpected application error type=[{}]", ex.getClass().getSimpleName());
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(BaseResponseDTO.error("Internal server error"));
	}
}
