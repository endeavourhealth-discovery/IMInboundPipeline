package org.endeavourhealth.pipeline.inbound.customexceptions;

public enum ErrorCodes {
  UNKNOWN_FORMAT_CONVERSION_EXCEPTION("UnknownFormatConversionException"),
  UNHANDLED_EXCEPTION("UnhandledException"),
  NO_HANDLER_FOUND_EXCEPTION("NoHandlerFoundException"),
  HTTP_MESSAGE_NOT_READABLE("HttpMessageNotReadable"),
  HTTP_REQUEST_METHOD_NOT_SUPPORTED("HttpRequestMethodNotSupported"),
  MISSING_SERVLET_REQUEST_PARAMETER("MissingServletRequestParameter"),
  TYPE_MISMATCH("TypeMismatch"),
  HTTP_MEDIA_TYPE_NOT_SUPPORTED("HttpMediaTypeNotSupported"),
  ACCESS_DENIED_EXCEPTION("AccessDeniedException"),
  AUTHENTICATION_EXCEPTION("AuthenticationException"),
  ILLEGAL_ARGUMENT_EXCEPTION("IllegalArgumentException");

  private String code;

  public String asString() {
    return code;
  }

  ErrorCodes(String code) {
    this.code = code;
  }
}
