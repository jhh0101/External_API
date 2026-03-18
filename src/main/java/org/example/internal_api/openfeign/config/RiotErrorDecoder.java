package org.example.internal_api.openfeign.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internal_api.global.error.CustomException;
import org.example.internal_api.global.error.ErrorCode;
import org.example.internal_api.global.error.RetryableCustomException;
import org.springframework.http.HttpStatusCode;

@Slf4j
@RequiredArgsConstructor
public class RiotErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        log.error("[Riot API ERROR] methodKey: {}, status: {}", methodKey, status);

        // 상태에 따른 에러 CustomException을 던짐
        if (status == 404) {
            throw new CustomException(ErrorCode.RIOT_USER_NOT_FOUND);
        }
        if (status == 429) {
            throw new RetryableCustomException(ErrorCode.RIOT_RATE_LIMIT_EXCEEDED);
        }

        // 5xx에러 한번에 처리
        if (HttpStatusCode.valueOf(status).is5xxServerError()) {
            throw new RetryableCustomException(ErrorCode.RIOT_SERVER_ERROR);
        }

        // 우리가 정의하지 않은 나머지 에러는 기본 디코더에게 전달
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
