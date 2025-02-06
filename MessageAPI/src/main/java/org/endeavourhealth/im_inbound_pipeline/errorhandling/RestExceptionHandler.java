package org.endeavourhealth.im_inbound_pipeline.errorhandling;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.ClientAbortException;
import org.endeavourhealth.im_inbound_pipeline.model.customexceptions.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Set;

//@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String message = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
    ApiError error = new ApiError(HttpStatus.NOT_FOUND, message, ex, ErrorCodes.NO_HANDLER_FOUND_EXCEPTION);
    return buildResponseEntity(error);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String message = "Request required input is missing or null";
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, message, ex, ErrorCodes.HTTP_MESSAGE_NOT_READABLE);
    return buildResponseEntity(error);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    StringBuilder builder = new StringBuilder();
    Set<HttpMethod> methods = ex.getSupportedHttpMethods();
    if (methods != null) methods.forEach(t -> builder.append(t + " "));
    String message = "Method: " + ex.getMethod() + " is not supported for this API. Supported methods are " + builder;
    ApiError error = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, message, ex, ErrorCodes.HTTP_REQUEST_METHOD_NOT_SUPPORTED);
    return buildResponseEntity(error);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String message = "Required parameter: " + ex.getParameterName() + " is missing";
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, message, ex, ErrorCodes.MISSING_SERVLET_REQUEST_PARAMETER);
    return buildResponseEntity(error);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String message = "Type mismatch. ";
    String type = null;
    Class<?> requiredType = ex.getRequiredType();
    if (requiredType != null) type = requiredType.getName();
    if (type != null) message += ex.getPropertyName() + " should be of type " + type;
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, message, ex, ErrorCodes.TYPE_MISMATCH);
    return buildResponseEntity(error);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    StringBuilder builder = new StringBuilder();
    String message;
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
    message = builder.substring(0, builder.length() - 2);
    ApiError error = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message, ex, ErrorCodes.HTTP_MEDIA_TYPE_NOT_SUPPORTED);
    return buildResponseEntity(error);
  }

  @ExceptionHandler(Throwable.class)
  protected ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
    ex.printStackTrace();
    String message = "Unhandled server error occurred";
    ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, message, new Exception("Unhandled server error"), ErrorCodes.UNHANDLED_EXCEPTION);
    return buildResponseEntity(error);
  }

  @ExceptionHandler(ClientAbortException.class)
  protected ResponseEntity<Object> handleClientAbortException(ClientAbortException ex, HttpServletRequest req, WebRequest request) {
    if ("/api/entity/public/search".equals(req.getRequestURI())) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return handleAll(ex, request);
    }
  }

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
    throw ex;
  }


  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex, ErrorCodes.ILLEGAL_ARGUMENT_EXCEPTION);
    return buildResponseEntity(error);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }
}
