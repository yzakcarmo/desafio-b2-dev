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

    private static final String TENANT_HEADER = "x-tenant";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String tenantCode = httpRequest.getHeader(TENANT_HEADER);

        // Libera endpoints que não precisam de tenant (actuator, swagger)
        String path = httpRequest.getRequestURI();
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (tenantCode == null || tenantCode.isBlank()) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("""
                    {
                        "status": 400,
                        "code": "TENANT-001",
                        "message": "Header x-tenant é obrigatório"
                    }
                    """);
            return;
        }

        try {
            TenantContext.setTenant(tenantCode.toUpperCase().trim());
            chain.doFilter(request, response);
        } finally {
            // CRÍTICO: sempre limpar para não vazar entre requests
            TenantContext.clear();
        }
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }
}