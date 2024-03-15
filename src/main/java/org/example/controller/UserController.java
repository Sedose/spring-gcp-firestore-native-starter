package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.document.RoleDocument;
import org.example.document.UserDocument;
import org.example.request.body.UserCreateRequestBody;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/roles")
    public Flux<RoleDocument> getAllUsersRoles() {
        return userService.getAllUsersRoles();
    }

    @GetMapping
    public Flux<UserDocument> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(params = "age")
    public Flux<UserDocument> getAllUsersByAge(Integer age) {
        return userService.getAllUsersByAge(age);
    }

    @GetMapping(params = { "age", "favoritePetName"} )
    public Flux<UserDocument> getAllUsersByAgeAndName(Integer age, String favoritePetName) {
        return userService.getAllUsersByAgeAndName(age, favoritePetName);
    }

    @GetMapping("/{userName}/roles")
    public Flux<RoleDocument> getUserRolesByUserName(@PathVariable("userName") String userName) {
        return userService.getUserRolesByUserName(userName);
    }

    @PostMapping
    public Mono<UserDocument> createNewUser(@RequestBody UserCreateRequestBody user) {
        return userService.createUser(user);
    }

    @PutMapping
    public Mono<UserDocument> updateUser(@RequestBody UserDocument user) {
        return userService.updateUser(user);
    }

    @DeleteMapping(params = "age")
    public Mono<Void> deleteUserByAge(Integer age) {
        return userService.deleteUsersByAge(age);
    }
}
