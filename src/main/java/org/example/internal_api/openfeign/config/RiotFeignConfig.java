package org.example.internal_api.openfeign.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RiotFeignConfig {
    @Value("${riot.api.key}")
    private String riotKey;

    // 헤더에 "X-Riot-Token"와 키를 넣어줌
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Riot-Token", riotKey);
        };
    }

    // Logger.Level: 통신 과정을 콘솔창에 얼마나 자세히 보여줄지 선택
    // FULL로 설정하면 요청 URL, 헤더 정보, 주고받은 바디 데이터까지 다 보여줌.(로컬 환경 권장)
    // (주의: 실무 운영 서버에서는 성능을 위해 NONE이나 BASIC으로 낮춥니다)
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
