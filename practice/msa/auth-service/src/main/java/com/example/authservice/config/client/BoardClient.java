package com.example.authservice.config.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "board-service", url = "${board-service.url}:http://localhost:8081")
public interface BoardClient {
    @DeleteMapping("/api/boards/internal/users/{userId}")
    void deleteUserContents(@PathVariable String userId);
}
