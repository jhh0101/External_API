package org.example.internal_api.global.dto;

public record MatchRecord(
        String championName, // 챔피언 이름 ("LeeSin", "Fiora" 등)
        boolean win,         // 승패 (true/false)
        int kills,           // 킬 수
        int deaths,          // 데스 수
        int assists,         // 어시스트 수
        int totalDamage      // 총 가한 피해량
) {}