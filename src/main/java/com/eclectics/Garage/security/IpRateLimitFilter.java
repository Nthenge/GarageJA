package com.eclectics.Garage.security;

import com.eclectics.Garage.configuration.RateLimitProperties;
import io.github.bucket4j.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class IpRateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties rateLimitProperties;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public IpRateLimitFilter(RateLimitProperties rateLimitProperties) {
        this.rateLimitProperties = rateLimitProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();

        Bucket bucket = buckets.computeIfAbsent(
                ip + ":" + path,
                k -> createBucketForPath(path)
        );

        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.getWriter().write("Too many requests for this endpoint");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Bucket createBucketForPath(String path) {
        RateLimitProperties.Limit limit = null;

        if (path.startsWith("/user/login")) limit = rateLimitProperties.getLimits().get("user-login");
        else if (path.startsWith("/user/register")) limit = rateLimitProperties.getLimits().get("user-register");
        else if (path.startsWith("/user/delete-account")) limit = rateLimitProperties.getLimits().get("user-delete-account");
        else if (path.startsWith("/user/verify-reset-token")) limit = rateLimitProperties.getLimits().get("user-verify-reset-token");
        else if (path.startsWith("/user/confirm")) limit = rateLimitProperties.getLimits().get("user-confirm");
        else if (path.startsWith("/user/update")) limit = rateLimitProperties.getLimits().get("user-update");
        else if (path.startsWith("/user/update-password")) limit = rateLimitProperties.getLimits().get("user-update-password");
        else if (path.startsWith("/user/register/mechanic")) limit = rateLimitProperties.getLimits().get("user-register-mechanic");
        else if (path.startsWith("/user/search")) limit = rateLimitProperties.getLimits().get("user-search");
        else limit = rateLimitProperties.getLimits().get("default");

        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(
                        limit.getCapacity(),
                        Refill.greedy(limit.getRefill(), Duration.ofMinutes(limit.getRefillPeriodMinutes()))
                ))
                .build();
    }
}
