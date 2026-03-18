package org.example.internal_api.openfeign.client;

import org.example.internal_api.openfeign.config.RiotFeignConfig;
import org.example.internal_api.global.dto.ChampionDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * @FeignClient
 * - name: 컨테이너에 등록될 빈(Bean)의 이름. 식별자 역할만 함.
 * - url: 기본적으로 요청을 보낼 베이스 주소
 * - configuration: 아까 만든 통신 규칙(API Key 헤더 자동 추가, 로깅)을 여기에 적용
 */
@FeignClient(name = "riotChampFeignApiClient", url = "${riot.api.champ}", configuration = RiotFeignConfig.class)
public interface RiotChampFeignApiClient {
    @GetMapping
    ChampionDataDto getChampData();
}
