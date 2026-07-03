package com.example.feignclient.client;

import com.example.feignclient.dto.DataRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

// * Feign Client 선언부
// - interface라서 우리가 직접 구현하지 않음
// 즉 "다른 서버로 HTTP 요청을 보내는 코드"를 인터페이스 선언만으로 대신 만들어주는 것

// * @FeignClient
// - name : 이 클라이언트의 고유 이름(Bean 이름), 필수값
// - url : 호출할 대상 서버 주소. ${feign-api.url}는 application.yml에서 값을 읽어옴
// (주소를 하드코딩하지 않고 설정으로 분리)

@FeignClient(name = "exampleClient", url = "${feign-api.url}")
public interface ExampleClient {
    @GetMapping("/{id}")
    String getData(@PathVariable Long id);

    @PostMapping
    String createData(@RequestBody DataRequest dataRequest);

    @PutMapping("/{id}")
    String updateData(@PathVariable Long id, @RequestBody DataRequest dataRequest);

    @DeleteMapping("/{id}")
    String deleteData(@PathVariable Long id);
}
