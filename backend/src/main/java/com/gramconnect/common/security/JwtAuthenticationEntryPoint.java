package com.gramconnect.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom AuthenticationEntryPoint returning standardized JSON ApiResponse on 401 Unauthorized errors.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        log.warn("Unauthorized access to URI: {} - Error: {}", request.getRequestURI(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = ErrorResponse.of(
                "UNAUTHORIZED_ACCESS",
                "Full authentication is required to access this resource: " + authException.getMessage()
        );

        ApiResponse<Void> apiResponse = ApiResponse.error("Unauthorized request", errorResponse);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
