package org.example.internal_api.global.error;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 고유 식별자 생성
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        // MDC 주머니에 'traceId'라는 이름으로 고유 식별자 삽입
        MDC.put("traceId", traceId);

        try {
            // 고유 식별자를 붙인 상태로 컨트롤러나 서비스에 요청을 보냄
            filterChain.doFilter(request, response);
        } finally {
            // 요청 처리가 끝나면 반드시 고유 식별자 삭제
            MDC.clear();
        }
    }
}
