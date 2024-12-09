package com.example.play_the_piano.global.exception;

import com.example.play_the_piano.global.common.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CommonResponse<ErrorResponse>> handleValidationExceptions(
		MethodArgumentNotValidException ex, HttpServletRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors()
			.forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
		for (Map.Entry<String, String> entry : errors.entrySet()) {
			String errorCode = entry.getKey();
			String errorMessage = entry.getValue();
			log.error(">>>MethodArgumentNotValidException<<< \n url: {}, errorCode: {}, msg: {}",
				request.getRequestURI(), errorCode, errorMessage);
		}
		return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler({IllegalArgumentException.class, NullPointerException.class,
		DuplicateKeyException.class})
	public ResponseEntity<CommonResponse<ErrorResponse>> handleBadRequestException(Exception ex,
		HttpServletRequest request) {
		log.error(">>>" + ex.getClass().getName() + "<<< \n msg: {}, code: {}, url: {}",
			ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI(), ex);
		return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
	public ResponseEntity<CommonResponse<ErrorResponse>> handleUnauthorizedException(Exception ex,
		HttpServletRequest request) {
		log.error(">>>" + ex.getClass().getName() + "<<< \n msg: {}, code: {}, url: {}",
			ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI(), ex);
		return createResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}


	private ResponseEntity<CommonResponse<ErrorResponse>> createResponse(HttpStatus status,
		String message) {
		ErrorResponse errorResponse = new ErrorResponse(status);
		return ResponseEntity.status(status)
			.body(CommonResponse.<ErrorResponse>builder()
				.data(errorResponse)
				.msg(message)
				.build());
	}
}
