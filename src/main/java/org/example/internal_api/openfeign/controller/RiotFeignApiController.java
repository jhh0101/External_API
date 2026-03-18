package org.example.internal_api.openfeign.controller;

import lombok.RequiredArgsConstructor;
import org.example.internal_api.global.dto.MatchRecord;
import org.example.internal_api.global.dto.MyMostChampionDto;
import org.example.internal_api.global.dto.RiotAccountResponse;
import org.example.internal_api.global.dto.RiotMatchDetailDto;
import org.example.internal_api.openfeign.service.RiotFeignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RiotFeignApiController {

    private final RiotFeignService riotFeignService;

    @GetMapping("api/riot/account/feign/{gameName}/{tagLine}")
    public RiotAccountResponse getRiotAccount(@PathVariable String gameName, @PathVariable String tagLine) {
        return riotFeignService.getAccountInfo(gameName, tagLine);
    }

    @GetMapping("api/matches/by-puuid/feign/{puuid}")
    public List<String> getMatchIds(@PathVariable String puuid) {
        return riotFeignService.getMatchIdsFeign(puuid);
    }

    @GetMapping("api/matches/by-puuid/matches/feign/{puuid}")
    public List<RiotMatchDetailDto.Participant> getMatches(@PathVariable String puuid) {
        return riotFeignService.getMatchIdsFeign2(puuid);
    }


    @GetMapping("api/matches/feign/{matchId}")
    public RiotMatchDetailDto getMatchDetail(@PathVariable String matchId) {
        return riotFeignService.getMatchDetailFeign(matchId);
    }

    @GetMapping("api/matches/list/feign/{matchIds}")
    public List<RiotMatchDetailDto> getMatchDetailList(@PathVariable List<String> matchIds) {
        return riotFeignService.getMatchDetailFeignAsync(matchIds);
    }


    @GetMapping("api/riot/account/feign/champ/{gameName}/{tagLine}")
    public List<MyMostChampionDto> getRiotAccount2(@PathVariable String gameName, @PathVariable String tagLine) {
        return riotFeignService.getAccountInfo2(gameName, tagLine);
    }

    @GetMapping("api/riot/account/feign/winner-matches/{gameName}/{tagLine}")
    public List<MatchRecord> getWinnerMatches(@PathVariable String gameName, @PathVariable String tagLine) {
        return riotFeignService.getWinnerMatches(gameName, tagLine);
    }
}
