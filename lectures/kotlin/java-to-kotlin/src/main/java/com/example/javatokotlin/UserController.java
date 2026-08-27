package com.example.javatokotlin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // C
    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody UserCreateRequest userCreateRequest
    ) {
        User user = userService.createUser(
                userCreateRequest.getName(),
                userCreateRequest.getEmail()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    // R
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // U
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUsername(@PathVariable Long id, @RequestParam String updateName){
        User user = userService.updateUsername(id, updateName);

        return ResponseEntity.ok(user);
    }
}
