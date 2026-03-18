package org.example.internal_api.webclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RiotWebClientConfig {
    @Value("${riot.api.url}")
    private String riotUrl;

    @Value("${riot.api.key}")
    private String riotKey;

    @Bean
    public WebClient riotWebClient() {
        return WebClient.builder()
                .baseUrl(riotUrl)
                .defaultHeader("X-Riot-Token", riotKey)
                .build();
    }
}
