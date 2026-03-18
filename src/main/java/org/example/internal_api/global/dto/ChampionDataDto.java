package org.example.internal_api.global.dto;

import java.util.Map;

public record ChampionDataDto(
        Map<String, InnerDto> data
) {
    public record InnerDto(
            String key,
            String name
            ){}
}
