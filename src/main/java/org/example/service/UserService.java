package org.example.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import com.google.cloud.spring.data.firestore.transaction.ReactiveFirestoreTransactionManager;
import lombok.RequiredArgsConstructor;
import org.example.document.RoleDocument;
import org.example.document.UserDocument;
import org.example.repository.UserRepository;
import org.example.request.body.UserCreateRequestBody;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Firestore firestore;
    private final FirestoreTemplate template;
    private final UserRepository userRepository;
    private final ReactiveFirestoreTransactionManager txManager;

    public Flux<RoleDocument> getUserRolesByUserName(String userName) {
        return userRepository.findByName(userName)
                .flatMap(it -> template.withParent(it).findAll(RoleDocument.class));
    }

    public Mono<UserDocument> createUser(UserCreateRequestBody requestBody) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(false);
        TransactionalOperator operator = TransactionalOperator.create(this.txManager, transactionDefinition);
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
                .flatMap(savedUser -> template.withParent(savedUser).saveAll(rolesFlux)
                        .then(Mono.just(savedUser))
                ).as(operator::transactional);
    }

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

    public Mono<UserDocument> createUserUnsafeWithoutTransaction(UserCreateRequestBody requestBody) {
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
