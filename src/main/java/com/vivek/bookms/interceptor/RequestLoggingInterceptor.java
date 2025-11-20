package com.vivek.bookms.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Request logging interceptor for enhanced monitoring and debugging
 * Logs incoming requests and their processing times
 */
@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        log.info("Incoming {} request to: {} from IP: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                getClientIpAddress(request));
        
        if (log.isDebugEnabled()) {
            log.debug("Request parameters: {}", request.getQueryString());
            log.debug("User-Agent: {}", request.getHeader("User-Agent"));
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        
        Object startTimeObj = request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTimeObj instanceof Long) {
            long startTime = (Long) startTimeObj;
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("Completed {} request to: {} - Status: {} - Duration: {}ms", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    response.getStatus(), 
                    duration);
            
            if (duration > 1000) {
                log.warn("Slow request detected: {} took {}ms", request.getRequestURI(), duration);
            }
        }
        
        if (ex != null) {
            log.error("Request failed with exception: {}", ex.getMessage(), ex);
        }
    }
    
    /**
     * Extract client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}