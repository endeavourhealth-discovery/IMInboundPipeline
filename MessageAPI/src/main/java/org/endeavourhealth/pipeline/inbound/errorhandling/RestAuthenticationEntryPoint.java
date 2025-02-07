package org.endeavourhealth.pipeline.inbound.errorhandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException ex) throws IOException, ServletException {
      httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
      try (OutputStream out = httpServletResponse.getOutputStream()) {
        out.write("Access forbidden".getBytes(StandardCharsets.UTF_8));
      }
  }
}
