package org.endeavourhealth.pipeline.inbound.errorhandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private static final Logger LOG = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

  @Override
  public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException ex) throws IOException, ServletException {
    LOG.warn("Authentication failed for user: {}", httpServletRequest.getRemoteUser());
    LOG.warn(ex.getMessage());
    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
    try (OutputStream out = httpServletResponse.getOutputStream()) {
      out.write("Access forbidden".getBytes(StandardCharsets.UTF_8));
    }
  }
}
