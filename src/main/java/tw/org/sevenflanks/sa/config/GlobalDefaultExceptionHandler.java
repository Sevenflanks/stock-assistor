package tw.org.sevenflanks.sa.config;

import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;
import tw.org.sevenflanks.sa.base.msg.exception.MsgException;
import tw.org.sevenflanks.sa.base.msg.model.MsgBody;

@Slf4j
@ControllerAdvice
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		if (body == null) {
			body = MsgBody.error(ex);
		}
		if (!(ex instanceof MsgException)
				&& !(ex instanceof IllegalStateException)
				&& !(ex instanceof IllegalArgumentException)
		) {
			log.error("request handling error", ex);
		}
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	@ExceptionHandler
	public ResponseEntity<Object> handleDefaultException(Exception ex, WebRequest request) {
		return handleExceptionInternal(
				ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		return handleExceptionInternal(
				ex,
				MsgTemplate.API0001.build(ex.getBindingResult()
						.getFieldErrors().stream()
						.map(FieldError::toString)
						.collect(Collectors.joining())),
				new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request);
	}

}
