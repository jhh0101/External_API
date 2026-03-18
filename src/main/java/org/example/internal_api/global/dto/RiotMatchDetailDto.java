package org.example.internal_api.global.dto;

import java.util.List;

public record RiotMatchDetailDto(
        Info info
) {
    // 2. info 상자 내부 (게임 시간, 모드 다 버리고 participants만 챙김)
    public record Info(
            List<Participant> participants
    ) {}

    // 3. 진짜 알맹이! 참가자 10명 중 내가 필요한 데이터만 쏙쏙 챙김
    public record Participant(
            String puuid,         // 누구의 전적인지 비교하기 위해 필요
            String championName,  // 챔피언 이름
            boolean win,          // 승패 여부
            int kills,
            int deaths,
            int assists,
            int totalDamageDealtToChampions
    ) {}
}