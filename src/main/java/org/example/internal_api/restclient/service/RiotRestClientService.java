package org.example.internal_api.restclient.service;

import lombok.RequiredArgsConstructor;
import org.example.internal_api.global.dto.MatchRecord;
import org.example.internal_api.global.dto.RiotAccountResponse;
import org.example.internal_api.global.dto.RiotMatchDetailDto;
import org.example.internal_api.global.error.CustomException;
import org.example.internal_api.global.error.ErrorCode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class RiotRestClientService {
    private final RestClient riotRestClient;

    public RiotAccountResponse getPuuid(String gameName, String tagLine) {
        String puuidUri = "/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}";

        return riotRestClient.get()
                .uri(puuidUri, gameName, tagLine)
                .retrieve()
                .body(RiotAccountResponse.class);
    }

    public List<String> getMatchIds(String puuid) {
        String matchIdsUri = "/lol/match/v5/matches/by-puuid/{puuid}/ids?start=0&count=10";

        return riotRestClient.get()
                .uri(matchIdsUri, puuid)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public RiotMatchDetailDto getMatchDetail(String matchId) {
        String matchIdDetailUri = "/lol/match/v5/matches/{matchId}";

        return riotRestClient.get()
                .uri(matchIdDetailUri, matchId)
                .retrieve()
                .body(RiotMatchDetailDto.class);
    }

    public List<MatchRecord> getWinnerGame(String gameName, String tagLine) {
        RiotAccountResponse accountResponse = this.getPuuid(gameName, tagLine);

        List<String> matchIds = this.getMatchIds(accountResponse.puuid());

        List<CompletableFuture<RiotMatchDetailDto>> futures = matchIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> {
                    return this.getMatchDetail(id);
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));

        List<RiotMatchDetailDto.Participant> details = futures.stream()
                .map(CompletableFuture::join)
                .map(dto -> extractMyParticipant(dto, accountResponse.puuid()))
                .toList();

        return details.stream()
                .map(dto -> new MatchRecord(
                        dto.championName(),
                        dto.win(),
                        dto.kills(),
                        dto.deaths(),
                        dto.assists(),
                        dto.totalDamageDealtToChampions()
                ))
                .filter(dto -> dto.win() && dto.kills() >= 10)
                .sorted(Comparator.comparing(MatchRecord::kills).reversed())
                .toList();
    }

    private RiotMatchDetailDto.Participant extractMyParticipant(RiotMatchDetailDto dto, String puuid) {
        return dto.info()
                .participants()
                .stream()
                .filter(p -> p.puuid().equals(puuid))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.RIOT_USER_NOT_FOUND));
    }

}
