package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.document.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Flux<User> getAllUsersByAge(Integer age) {
        return userRepository.findByAge(age);
    }

    public Mono<Void> deleteUsersByAge(Integer age) {
        return userRepository.findByAge(age)
                .flatMap(userRepository::delete)
                .then();
    }

    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    public Mono<User> updateUser(User user) {
        return userRepository.save(user);
    }
}
