package org.example.internal_api.restclient.config;

import org.example.internal_api.global.error.CustomException;
import org.example.internal_api.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {
    @Value("${riot.api.url}")
    private String riotUrl;

    @Value("${riot.api.key}")
    private String riotKey;

    @Bean
    public RestClient riotRedisClient() {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(5));


        return RestClient.builder()
                .baseUrl(riotUrl)
                .defaultHeader("X-Riot-Token", riotKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new CustomException(ErrorCode.RIOT_USER_NOT_FOUND);
                }))
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new CustomException(ErrorCode.RIOT_SERVER_ERROR);
                }))
                .build();
    }
}
