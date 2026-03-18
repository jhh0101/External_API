package org.example.internal_api.openfeign.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internal_api.global.dto.*;
import org.example.internal_api.global.error.CustomException;
import org.example.internal_api.global.error.ErrorCode;
import org.example.internal_api.global.error.RetryableCustomException;
import org.example.internal_api.openfeign.client.RiotChampFeignApiClient;
import org.example.internal_api.openfeign.client.RiotFeignApiClient;
import org.example.internal_api.openfeign.client.RiotKrFeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiotFeignService {

    private final RiotFeignApiClient riotFeignApiClient;
    private final RiotKrFeignClient riotKrFeignClient;
    private final RiotChampFeignApiClient riotChampFeignApiClient;

    private Map<Integer, String> championNameMap = new HashMap<>();

    @Retryable(
            retryFor = RetryableCustomException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public RiotAccountResponse getAccountInfo(String gameName, String tagLine) {
        return riotFeignApiClient.getAccountInfo(gameName, tagLine);
    }

    @Recover
    public RiotAccountResponse getAccountInfoRecover(RetryableCustomException e, String gameName, String tagLine) {
        log.error("[Riot Account 호출 오류] message: {}", e.getErrorCode().getMessage());
        throw e;
    }

    @Retryable(
            retryFor = RetryableCustomException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public List<String> getMatchIdsFeign(String puuid) {
        return riotFeignApiClient.getMatchIdsFeign(puuid);
    }

    @Recover
    public List<String> getMatchIdsRecover(RetryableCustomException e, String puuid) {
        log.error("[Riot 매치 리스트 조회 오류] message: {}", e.getErrorCode().getMessage());
        throw e;
    }

    @Retryable(
            retryFor = RetryableCustomException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public RiotMatchDetailDto getMatchDetailFeign(String matchId) {
        return riotFeignApiClient.getMatchDetailFeign(matchId);
    }

    @Recover
    public RiotMatchDetailDto getMatchDetailRecover(RetryableCustomException e, String matchId) {
        log.error("[Riot 매치 상세 조회 오류] message: {}", e.getErrorCode().getMessage());
        throw e;
    }

    @Retryable(
            retryFor = RetryableCustomException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public List<RiotMatchDetailDto> getMatchDetailFeignAsync(List<String> matchIds) {
        List<CompletableFuture<RiotMatchDetailDto>> futures = matchIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> getMatchDetailFeign(id)))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    @Recover
    public List<RiotMatchDetailDto> getMatchDetailAsyncRecover(RetryableCustomException e, List<String> matchId) {
        log.error("[Riot 매치 상세 리스트 조회 오류] message: {}", e.getErrorCode().getMessage());
        throw e;
    }

    @Retryable(
            retryFor = RetryableCustomException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public List<RiotMatchDetailDto.Participant> getMatchIdsFeign2(String puuid) {
        List<String> matches = riotFeignApiClient.getMatchIdsFeign(puuid);
        List<CompletableFuture<RiotMatchDetailDto>> futures = matches.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> getMatchDetailFeign(id)))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .map(dto -> {
                    return dto.info()
                            .participants()
                            .stream()
                            .filter(p -> p.puuid().equals(puuid))
                            .findFirst()
                            .orElseThrow(() -> new CustomException(ErrorCode.RIOT_USER_NOT_FOUND));
                })
                .toList();
    }

    @Recover
    public List<RiotMatchDetailDto.Participant> getMatchIdsRecover2(RetryableCustomException e, String puuid) {
        log.error("[Riot 매치 리스트 조회 오류] message: {}", e.getErrorCode().getMessage());
        throw e;
    }




    @PostConstruct // 서버 시작하자마자 실행
    public void initChampionMap() {
        // 모든 챔피언 데이터를 가져와서 dto로 변환
        ChampionDataDto dataDto = riotChampFeignApiClient.getChampData();

        for (ChampionDataDto.InnerDto data : dataDto.data().values()) {
            int champId = Integer.parseInt(data.key());
            String champName = data.name();
            championNameMap.put(champId, champName);
        }
        log.info("라이엇 챔피언 데이터 저장 완료");

    }

    // 플레이어의 숙련도가 가장 높은 챔피언 5개 출력(이름, 점수)
    @Retryable(
            retryFor = RetryableCustomException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public List<MyMostChampionDto> getAccountInfo2(String gameName, String tagLine) {
        // 1. 플레이어 검색
        RiotAccountResponse accountInfo = riotFeignApiClient.getAccountInfo(gameName, tagLine);
        String puuid = accountInfo.puuid();
        // 2. 플레이어의 puuid로 숙련도가 가장 높은 챔피언 5개 호출
        List<MasteryDto> mostChamp = riotKrFeignClient.getMostChamp(puuid, 5);


        return mostChamp.stream()
                .map(dto -> {
                    String champName = championNameMap.get(dto.championId());
                    int champPoint = dto.championPoints();
                    // 가져온 값을 최종 출력 dto로 변환
                    return new MyMostChampionDto(champName, champPoint);
                })
                .toList();
    }

    @Recover
    public List<MyMostChampionDto> getAccountInfoRecover2(RetryableCustomException e, String gameName, String tagLine) {
        log.error("[Riot Account 호출 오류] message: {}", e.getErrorCode().getMessage());
        throw e;
    }


    // 플레이어가 10킬 이상으로 이긴 매치 조회
    public List<MatchRecord> getWinnerMatches(String gameName, String tagLine) {
        // 1. 플레이어 검색
        RiotAccountResponse accountInfo = riotFeignApiClient.getAccountInfo(gameName, tagLine);
        String puuid = accountInfo.puuid();
        List<String> matchIds = riotFeignApiClient.getMatchIdsFeign(puuid);

        List<CompletableFuture<RiotMatchDetailDto>> futures = matchIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> riotFeignApiClient.getMatchDetailFeign(id)))
                .toList();
        List<RiotMatchDetailDto.Participant> details = futures.stream()
                .map(CompletableFuture::join)
                .map(dto -> {
                    return dto.info()
                            .participants()
                            .stream()
                            .filter(p -> p.puuid().equals(puuid))
                            .findFirst()
                            .orElseThrow(() -> new CustomException(ErrorCode.RIOT_USER_NOT_FOUND));
                })
                .toList();

        return details.stream()
                .filter(dto -> dto.win() && dto.kills() >= 10)
                .map(dto -> new MatchRecord(
                        dto.championName(),
                        dto.win(),
                        dto.kills(),
                        dto.deaths(),
                        dto.assists(),
                        dto.totalDamageDealtToChampions()))
                .sorted(Comparator.comparing(MatchRecord::kills).reversed())
                .limit(3)
                .toList();
    }

    public int getTotalDamageWithAhri(List<MatchRecord> matchRecords) {
        return matchRecords.stream()
                .filter(matchRecord -> matchRecord.championName().equals("Ahri"))
                .mapToInt(MatchRecord::totalDamage)
                .sum();
    }

    public Map<String, Long> getChampMatchCount (List<MatchRecord> matchRecords) {
        return matchRecords.stream()
                .collect(Collectors.groupingBy(
                        MatchRecord::championName, // 그룹으로 묶을 키
                        Collectors.counting()      // 키의 값(championName을 기준으로 카운트)
                ));
    }
}
