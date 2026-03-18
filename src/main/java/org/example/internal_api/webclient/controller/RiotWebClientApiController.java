package org.example.internal_api.webclient.controller;

import lombok.RequiredArgsConstructor;
import org.example.internal_api.webclient.dto.RiotAccountResponse;
import org.example.internal_api.webclient.service.RiotWebClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RiotWebClientApiController {

    private final RiotWebClientService riotApiService;

    @GetMapping("api/riot/account/web-client/{gameName}/{tagLine}")
    public RiotAccountResponse getRiotAccount(@PathVariable String gameName, @PathVariable String tagLine) {
        return riotApiService.getAccountInfo(gameName, tagLine);
    }

    @GetMapping("api/matches/by-puuid/web-client/{puuid}")
    public List<String> getMatchIds(@PathVariable String puuid) {
        return riotApiService.getMatchIds(puuid);
    }

    @GetMapping("api/matches/by-puuid/web-client-async/{puuid}")
    public Mono<List<String>> getMatchIdsAsync(@PathVariable String puuid) {
        return riotApiService.getMatchIdsAsync(puuid);
    }
//
//    @GetMapping("api/matches/web-client/{matchId}")
//    public Object getMatchDetail(@PathVariable String matchId) {
//        return riotApiService.getMatchDetail(matchId);
//    }
}
