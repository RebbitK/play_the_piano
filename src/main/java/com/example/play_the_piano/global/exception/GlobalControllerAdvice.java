package com.example.play_the_piano.global.exception;

import com.example.play_the_piano.global.common.CommonResponse;
import com.example.play_the_piano.global.exception.custom.Base64ConversionException;
import com.example.play_the_piano.global.exception.custom.EmailAlreadyRegisteredException;
import com.example.play_the_piano.global.exception.custom.InvalidAuthCodeException;
import com.example.play_the_piano.global.exception.custom.InvalidBase64ExceptionException;
import com.example.play_the_piano.global.exception.custom.InvalidPositionException;
import com.example.play_the_piano.global.exception.custom.PasswordUpdateFailedException;
import com.example.play_the_piano.global.exception.custom.PostNotFoundException;
import com.example.play_the_piano.global.exception.custom.S3Exception;
import com.example.play_the_piano.global.exception.custom.SendEmailException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<CommonResponse<ErrorResponse>> handleValidationExceptions(
		MethodArgumentNotValidException ex, HttpServletRequest request) {
		String errorMessage = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage()
				: "Unknown validation error")
			.findFirst()
			.orElse("Validation failed");
		return createResponse(HttpStatus.BAD_REQUEST, errorMessage);
	}


	@ExceptionHandler({IllegalArgumentException.class, NullPointerException.class,
		DuplicateKeyException.class, PasswordUpdateFailedException.class,
		EmailAlreadyRegisteredException.class, S3Exception.class, InvalidPositionException.class,
		InvalidBase64ExceptionException.class})
	public ResponseEntity<CommonResponse<ErrorResponse>> handleBadRequestException(Exception ex,
		HttpServletRequest request) {
		log.error(">>>" + ex.getClass().getName() + "<<< \n msg: {}, code: {}, url: {}",
			ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI(), ex);
		return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler({PostNotFoundException.class})
	public ResponseEntity<CommonResponse<ErrorResponse>> handlePostNotFoundException(Exception ex,
		HttpServletRequest request) {
		log.error(">>>" + ex.getClass().getName() + "<<< \n msg: {}, code: {}, url: {}",
			ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI(), ex);
		return createResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class,
		InvalidAuthCodeException.class})
	public ResponseEntity<CommonResponse<ErrorResponse>> handleUnauthorizedException(Exception ex,
		HttpServletRequest request) {
		log.error(">>>" + ex.getClass().getName() + "<<< \n msg: {}, code: {}, url: {}",
			ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI(), ex);
		return createResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler({SendEmailException.class, Base64ConversionException.class})
	public ResponseEntity<CommonResponse<ErrorResponse>> handleInternalServerErrorException(
		Exception ex,
		HttpServletRequest request) {
		log.error(">>>" + ex.getClass().getName() + "<<< \n msg: {}, code: {}, url: {}",
			ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), ex);
		return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
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
