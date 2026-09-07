package com.clinic.Gateway_Service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filter de Rate Limiting para protección Anti-DDoS en el API Gateway.
 * Restringe las peticiones entrantes a un máximo de 20 peticiones por segundo por IP.
 * Utiliza Upstash Redis distribuido con degradación elegante a memoria local en caso de desconexión.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimiterFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_SECOND = 20;
    private static final String RATE_LIMIT_PREFIX = "ratelimit:ip:";

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    // Fallback en memoria en caso de desconexión temporal de Upstash Redis
    private final ConcurrentHashMap<String, LocalWindow> localRateLimitMap = new ConcurrentHashMap<>();

    private static class LocalWindow {
        final long epochSecond;
        final AtomicInteger count;

        LocalWindow(long epochSecond, int initialCount) {
            this.epochSecond = epochSecond;
            this.count = new AtomicInteger(initialCount);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Permitir OPTIONS (pre-flight de CORS) sin penalizar el rate limit
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = extractClientIp(request);
        long currentSecond = Instant.now().getEpochSecond();
        long currentCount;

        try {
            if (redisTemplate != null) {
                String key = RATE_LIMIT_PREFIX + clientIp + ":" + currentSecond;
                Long count = redisTemplate.opsForValue().increment(key);
                if (count != null && count == 1) {
                    redisTemplate.expire(key, Duration.ofSeconds(2));
                }
                currentCount = (count != null) ? count : 1;
            } else {
                currentCount = checkLocalRateLimit(clientIp, currentSecond);
            }
        } catch (Exception e) {
            log.warn("Error comunicándose con Upstash Redis para Rate Limiting ({}), usando fallback local: {}", clientIp, e.getMessage());
            currentCount = checkLocalRateLimit(clientIp, currentSecond);
        }

        // Agregar headers informativos de Rate Limit
        response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_SECOND));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, MAX_REQUESTS_PER_SECOND - currentCount)));

        if (currentCount > MAX_REQUESTS_PER_SECOND) {
            log.warn("Rate limit excedido para la IP {} ({} req/s). Bloqueando con HTTP 429.", clientIp, currentCount);
            response.setStatus(429); // 429 Too Many Requests
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Retry-After", "1");
            response.getWriter().write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "L\u00edmite de peticiones excedido. M\u00e1ximo 20 peticiones por segundo por IP.",
                    "ip": "%s",
                    "retryAfterSeconds": 1
                }
            """.formatted(clientIp));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private long checkLocalRateLimit(String clientIp, long currentSecond) {
        // Limpieza básica periódica de entradas viejas
        if (localRateLimitMap.size() > 1000) {
            localRateLimitMap.entrySet().removeIf(entry -> entry.getValue().epochSecond < (currentSecond - 2));
        }

        LocalWindow window = localRateLimitMap.compute(clientIp, (ip, existing) -> {
            if (existing == null || existing.epochSecond != currentSecond) {
                return new LocalWindow(currentSecond, 1);
            } else {
                existing.count.incrementAndGet();
                return existing;
            }
        });

        return window != null ? window.count.get() : 1;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr != null) ? remoteAddr : "unknown";
    }
}
