package org.endeavourhealth.pipeline.inbound.errorhandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
  private static final Logger LOG = LoggerFactory.getLogger(RestAccessDeniedHandler.class);

  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
    LOG.warn("Access denied for user: {}", httpServletRequest.getRemoteUser());
    LOG.warn(e.getMessage());
    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    try (OutputStream out = httpServletResponse.getOutputStream()) {
      out.write("Access denied".getBytes(StandardCharsets.UTF_8));
    }
  }
}
