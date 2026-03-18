package org.example.internal_api.webclient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internal_api.global.error.CustomException;
import org.example.internal_api.global.error.ErrorCode;
import org.example.internal_api.webclient.dto.RiotAccountResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiotWebClientService {

    private final WebClient riotWebClient;

    public RiotAccountResponse getAccountInfo(String gameName, String tagLine) {
        String uri = "/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}";

        log.info("[Riot API 호출] 닉네임 : {}, 태그 : {}, 엔드 포인트 : {}", gameName, tagLine, uri);

        return riotWebClient.get()
                .uri(uri, gameName, tagLine)
                .retrieve() // 통신 시작 (RestTemplate의 exchange/getForObject 역할) / exchangeToMono(): 응답 바디뿐만 아니라, 응답 헤더(Header)나 상태 코드(Status) 전체를 내가 직접 까보고 조작 할 때 사용
                //WebClient는 onStatus로 에러를 낚아챔
                .onStatus(HttpStatusCode::is4xxClientError, response -> { // 4xx번대 에러는 서비스에서 사용자에게 보여줄 부분만 나눠서 처리
                    log.error("4xx에러 발생");
                    return Mono.error(new CustomException(ErrorCode.RIOT_USER_NOT_FOUND));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> { // 5xx번대 에러는 외부 API 서버 문제기 때문에 하나로 묶어서 처리
                    log.error("5xx에러 발생");
                    return Mono.error(new CustomException(ErrorCode.RIOT_SERVER_ERROR));
                })
                .bodyToMono(RiotAccountResponse.class)// 결과를 Mono 객체로 감싸기
                .block(); // 동기로 변환
    }

    public List<String> getMatchIds(String puuid) {
        String uri = "/lol/match/v5/matches/by-puuid/{puuid}/ids?start=0&count=20";

        log.info("[LOL 매치 리스트 조회] PUUID : {}", puuid);

        return riotWebClient.get()
                .uri(uri, puuid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("4xx에러 발생");
                    return Mono.error(new CustomException(ErrorCode.RIOT_USER_NOT_FOUND));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("5xx에러 발생");
                    return Mono.error(new CustomException(ErrorCode.RIOT_SERVER_ERROR));
                })
                .bodyToMono(String[].class)
                .map(Arrays::asList)
                .block();
    }

    public Mono<List<String>> getMatchIdsAsync(String puuid) {
        String uri = "/lol/match/v5/matches/by-puuid/{puuid}/ids?start=0&count=20";

        log.info("[LOL 매치 리스트 조회] PUUID : {}", puuid);

        return riotWebClient.get()
                .uri(uri, puuid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("4xx에러 발생");
                    return Mono.error(new CustomException(ErrorCode.RIOT_USER_NOT_FOUND));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("5xx에러 발생");
                    return Mono.error(new CustomException(ErrorCode.RIOT_SERVER_ERROR));
                })
                .bodyToMono(String[].class)
                .map(Arrays::asList);
                // .subscribe(success -> {                          // subscribe()은 응답 없이 백엔드에서만 처리하고 싶을 때 사용
                //      // 첫 번째 파라미터는 성공했을 때 백엔드 처리
                // },
                // error -> {
                //      // 두 번째 파라미터는 에러가 터졌을 때 백엔드 처리
                // })
    }
}
