package td.agricoles.agricol.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiKeyFilter implements Filter {

    @Value("${api.key}")
    private String validApiKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String apiKey = httpRequest.getHeader("x-api-key");

        if (apiKey == null || !apiKey.equals(validApiKey)) {
            httpResponse.setStatus(401);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Bad credentials\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}