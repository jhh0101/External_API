package org.example.internal_api.openfeign.client;

import org.example.internal_api.openfeign.config.RiotFeignConfig;
import org.example.internal_api.global.dto.RiotAccountResponse;
import org.example.internal_api.global.dto.RiotMatchDetailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/*
 * @FeignClient
 * - name: 컨테이너에 등록될 빈(Bean)의 이름. 식별자 역할만 함.
 * - url: 기본적으로 요청을 보낼 베이스 주소
 * - configuration: 아까 만든 통신 규칙(API Key 헤더 자동 추가, 로깅)을 여기에 적용
 */
@FeignClient(name = "riotFeignApiClient", url = "${riot.api.url}", configuration = RiotFeignConfig.class)
public interface RiotFeignApiClient {

    /*
     * @GetMapping: 이 URL로 HTTP GET 요청을 보낸다는 뜻
     * @PathVariable: 메서드의 파라미터 값을 URL의 {중괄호} 부분에 끼워 넣음
     * GET https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}
     * 으로 알아서 통신이 날아가고, 그 결과를 RiotAccountResponse 객체로 바꿔서 리턴함
     */
    @GetMapping("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
    RiotAccountResponse getAccountInfo(@PathVariable String gameName, @PathVariable String tagLine);

    @GetMapping("/lol/match/v5/matches/by-puuid/{puuid}/ids?start=0&count=10")
    List<String> getMatchIdsFeign(@PathVariable String puuid);

    @GetMapping("/lol/match/v5/matches/{matchId}")
    RiotMatchDetailDto getMatchDetailFeign(@PathVariable String matchId);

}
