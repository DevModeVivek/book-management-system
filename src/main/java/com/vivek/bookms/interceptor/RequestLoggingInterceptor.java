package com.vivek.bookms.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = generateRequestId();
        request.setAttribute("requestId", requestId);
        request.setAttribute("startTime", System.currentTimeMillis());
        
        logger.info("Request ID: {} | {} {} | Remote Address: {} | User-Agent: {} | Start Time: {}", 
                   requestId,
                   request.getMethod(), 
                   request.getRequestURI(),
                   request.getRemoteAddr(),
                   request.getHeader("User-Agent"),
                   LocalDateTime.now());
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestId = (String) request.getAttribute("requestId");
        Long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        
        logger.info("Request ID: {} | Response Status: {} | Duration: {}ms | Completed at: {}", 
                   requestId,
                   response.getStatus(),
                   duration,
                   LocalDateTime.now());
        
        if (ex != null) {
            logger.error("Request ID: {} | Exception occurred: {}", requestId, ex.getMessage(), ex);
        }
    }
    
    private String generateRequestId() {
        return "REQ-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }
}