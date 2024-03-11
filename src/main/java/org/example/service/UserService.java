package org.example.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import lombok.RequiredArgsConstructor;
import org.example.document.RoleDocument;
import org.example.document.UserDocument;
import org.example.repository.UserRepository;
import org.example.request.body.UserCreateRequestBody;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Firestore firestore;
    private final FirestoreTemplate template;
    private final UserRepository userRepository;

//    public Flux<User> query() {
//        template.withParent()
//    }

    public Flux<UserDocument> getAllUsers() {
        return userRepository.findAll();
    }

    public Flux<UserDocument> getAllUsersByAge(Integer age) {
        return userRepository.findByAge(age);
    }

    public Mono<Void> deleteUsersByAge(Integer age) {
        return userRepository.findByAge(age)
                .flatMap(userRepository::delete)
                .then();
    }

    public Mono<UserDocument> createUser(UserCreateRequestBody requestBody) {
        var user = UserDocument.builder()
                .name(requestBody.name())
                .age(requestBody.age())
                .phoneNumbers(requestBody.phoneNumbers())
                .build();
        var rolesFlux = Flux.fromStream(
                requestBody.roles().stream()
                        .map(it -> RoleDocument.builder().name(it.name()).build())
        );
        return userRepository.save(user)
                .flatMap(savedUser ->
                        template.withParent(savedUser).saveAll(rolesFlux)
                                .then(Mono.just(savedUser))
                );
    }


    public Mono<UserDocument> updateUser(UserDocument user) {
        return userRepository.save(user);
    }
}
