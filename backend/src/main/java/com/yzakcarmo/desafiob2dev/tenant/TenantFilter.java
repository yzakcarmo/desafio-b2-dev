package com.yzakcarmo.desafiob2dev.tenant;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class TenantFilter implements Filter {

    private static final String TENANT_HEADER        = "x-tenant";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Libera endpoints que não precisam de tenant (actuator, swagger)
        String path = httpRequest.getRequestURI();
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        String tenantCode     = httpRequest.getHeader(TENANT_HEADER);
        String authorization  = httpRequest.getHeader(AUTHORIZATION_HEADER);

        if (tenantCode == null || tenantCode.isBlank()) {
            writeError(httpResponse, HttpServletResponse.SC_BAD_REQUEST,
                    "TENANT-001", "Header x-tenant é obrigatório");
            return;
        }

        if (authorization == null || authorization.isBlank()) {
            writeError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "AUTH-001", "Header Authorization é obrigatório");
            return;
        }

        try {
            TenantContext.setTenant(tenantCode.toUpperCase().trim());
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    private void writeError(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"status\":" + status + ",\"code\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }
}