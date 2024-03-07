package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.document.User;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(params = "age")
    public Flux<User> getAllUsersByAge(Integer age) {
        return userService.getAllUsersByAge(age);
    }

    @PostMapping
    public Mono<User> createNewUser(User user) {
        return userService.createUser(user);
    }

    @DeleteMapping(params = "age")
    public Mono<Void> deleteUserByAge(Integer age) {
        return userService.deleteUsersByAge(age);
    }
}
