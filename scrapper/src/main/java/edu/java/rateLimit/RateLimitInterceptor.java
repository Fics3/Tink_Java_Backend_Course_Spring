package edu.java.rateLimit;

import edu.java.exception.RateLimitScrapperException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String ip = request.getRemoteAddr();

        Bucket tokenBucket = rateLimitService.resolveBucket(ip);
        if (tokenBucket.tryConsume(1)) {
            return true;
        } else {
            throw new RateLimitScrapperException("Превышено количество запросов", "Подождите и попробуйте снова");
        }
    }
}
