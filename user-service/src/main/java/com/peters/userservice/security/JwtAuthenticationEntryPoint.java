package com.peters.userservice.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String errorMessage = getErrorMessage(authException);

        String jsonResponse = String.format("{\"status\": \"%s\", \"message\": \"%s\", \"data\": \"%s\"}", false, errorMessage, authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }

    private static String getErrorMessage(AuthenticationException authException) {
        String errorMessage = "Access denied";

        if (authException.getMessage() != null && authException.getMessage().contains("expired")) {
            errorMessage = "JWT token has expired";
        } else if (authException.getMessage() != null && authException.getMessage().contains("signature")) {
            errorMessage = "Invalid JWT token";
        } else if (authException.getMessage() != null && authException.getMessage().contains("disabled")) {
            errorMessage = "User account is disabled";
        } // Add more conditions for other exception types if needed
        return errorMessage;
    }
}
