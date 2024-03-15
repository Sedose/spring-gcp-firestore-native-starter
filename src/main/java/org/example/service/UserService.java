package org.example.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import com.google.cloud.spring.data.firestore.transaction.ReactiveFirestoreTransactionManager;
import lombok.RequiredArgsConstructor;
import org.example.document.RoleDocument;
import org.example.document.UserDocument;
import org.example.repository.UserRepository;
import org.example.request.body.UserCreateRequestBody;
import org.example.util.ApiFutureUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Firestore firestore;
    private final FirestoreTemplate template;
    private final UserRepository repository;
    private final ReactiveFirestoreTransactionManager transactionManager;

    public Flux<UserDocument> getAllUsersByAgeAndName(Integer age, String favoritePetName) {
        CollectionReference users = firestore.collection("users");
        ApiFuture<QuerySnapshot> future = users
                .whereEqualTo("age", age)
                .whereEqualTo("favoritePetName", favoritePetName)
                .get();
        return ApiFutureUtil.toMono(future)
                .map(it -> it.toObjects(UserDocument.class))
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<RoleDocument> getUserRolesByUserName(String userName) {
        return repository.findByName(userName)
                .flatMap(it -> template.withParent(it).findAll(RoleDocument.class));
    }

    public Flux<RoleDocument> getAllUsersRoles() {
        return ApiFutureUtil.toMono(firestore.collectionGroup("roles").get())
                .map(it -> Flux.fromIterable(it.getDocuments()))
                .flatMapMany(Function.identity())
                .map(it -> it.toObject(RoleDocument.class));
    }

    public Mono<UserDocument> createUser(UserCreateRequestBody requestBody) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(false);
        TransactionalOperator operator = TransactionalOperator.create(this.transactionManager, transactionDefinition);
        var user = UserDocument.builder()
                .name(requestBody.name())
                .favoritePetName(requestBody.favoritePetName())
                .age(requestBody.age())
                .phoneNumbers(requestBody.phoneNumbers())
                .build();
        var rolesFlux = Flux.fromStream(
                requestBody.roles().stream()
                        .map(it -> RoleDocument.builder().name(it.name()).build())
        );
        return repository.save(user)
                .flatMap(savedUser -> template.withParent(savedUser).saveAll(rolesFlux)
                        .then(Mono.just(savedUser))
                ).as(operator::transactional);
    }

    public Flux<UserDocument> getAllUsers() {
        return repository.findAll();
    }

    public Flux<UserDocument> getAllUsersByAge(Integer age) {
        return repository.findByAge(age);
    }

    public Mono<Void> deleteUsersByAge(Integer age) {
        return repository.findByAge(age)
                .flatMap(repository::delete)
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
        return repository.save(user)
                .flatMap(savedUser ->
                        template.withParent(savedUser).saveAll(rolesFlux)
                                .then(Mono.just(savedUser))
                );
    }


    // TODO implement full
    public Mono<UserDocument> updateUser(UserDocument user) {
        return repository.save(user);
    }
}
