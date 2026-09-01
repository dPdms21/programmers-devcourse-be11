package com.example.boardservice.client;

import com.example.boardservice.dto.UserNameResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "auth-service", url = "${auth-service.url:http://localhost:8082}")
public interface AuthClient {
    @GetMapping("/api/users/names")
    List<UserNameResponseDto> getUserNames(@RequestParam("userIds") List<String> userIds);
}
