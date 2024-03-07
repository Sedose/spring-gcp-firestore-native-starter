package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.document.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository exampleRepository;

    public Flux<User> getAllUsers() {
        return exampleRepository.findAll();
    }
}
