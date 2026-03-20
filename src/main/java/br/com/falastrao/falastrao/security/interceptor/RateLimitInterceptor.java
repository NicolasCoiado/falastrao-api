package br.com.falastrao.falastrao.security.interceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket(int capacity, int refillAmount, Duration duration) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillIntervally(refillAmount, duration)
                        .build())
                .build();
    }

    private Bucket getBucket(String key, int capacity, int refill, Duration duration) {
        return buckets.computeIfAbsent(key, k -> createBucket(capacity, refill, duration));
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String route = method + ":" + path;

        Bucket bucket = switch (route) {
            case "POST:/auth/login"               -> getBucket(ip + ":login",          5,  5,  Duration.ofMinutes(1));
            case "POST:/users"                    -> getBucket(ip + ":register",        3,  3,  Duration.ofMinutes(1));
            case "POST:/auth/resend-verification" -> getBucket(ip + ":resend",          3,  3,  Duration.ofMinutes(10));
            case "POST:/topics/suggest"           -> getBucket(ip + ":suggest",         10, 10, Duration.ofMinutes(1));
            case "GET:/topics/search"             -> getBucket(ip + ":search",          30, 30, Duration.ofMinutes(1));
            case "POST:/reviews"                  -> getBucket(ip + ":review",          10, 10, Duration.ofMinutes(1));
            default                               -> getBucket(ip + ":default",         60, 60, Duration.ofMinutes(1));
        };

        if (bucket.tryConsume(1)) {
            return true;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write("Too many requests");
        return false;
    }
}
