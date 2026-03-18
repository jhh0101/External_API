package org.example.internal_api.openfeign.client;

import org.example.internal_api.global.dto.MasteryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "riotKrClient", url = "https://kr.api.riotgames.com") // 주소가 kr 입니다!
public interface RiotKrFeignClient {

    @GetMapping("/lol/champion-mastery/v4/champion-masteries/by-puuid/{encryptedPUUID}/top")
    List<MasteryDto> getMostChamp(@PathVariable String encryptedPUUID, @RequestParam int count);
}