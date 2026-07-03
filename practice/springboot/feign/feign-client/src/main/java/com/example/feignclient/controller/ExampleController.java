package com.example.feignclient.controller;

import com.example.feignclient.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feign/data")
public class ExampleController {
    private final ExampleService exampleService;

    @GetMapping("/{id}")
    public String getData(@PathVariable Long id) {
        return exampleService.getDataById(id);
    }

    @PostMapping
    public String createData(@RequestParam String name, @RequestParam int value) {
        return exampleService.createData(name, value);
    }

    @PutMapping("/{id}")
    public String updateData(@PathVariable Long id, @RequestParam String name, @RequestParam int value) {
        return exampleService.updateData(id, name, value);
    }

    @DeleteMapping("/{id}")
    public String deleteData(@PathVariable Long id) {
        return exampleService.deleteData(id);
    }
}
