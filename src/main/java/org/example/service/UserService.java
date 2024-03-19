package org.example.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.AggregateQuery;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import com.google.cloud.spring.data.firestore.transaction.ReactiveFirestoreTransactionManager;
import lombok.RequiredArgsConstructor;
import org.example.document.RoleDocument;
import org.example.document.UserDocument;
import org.example.document.UserName;
import org.example.repository.UserRepository;
import org.example.request.body.UserCreateRequestBody;
import org.example.util.ApiFutureUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Firestore firestore;
    private final FirestoreTemplate template;
    private final UserRepository repository;
    private final ReactiveFirestoreTransactionManager transactionManager;

    public Flux<UserDocument> getAllUsersByAgeMinAndName(Integer age, String favoritePetName, String phoneNumber) {
        CollectionReference users = firestore.collection("users");
        ApiFuture<QuerySnapshot> future = users
                .whereGreaterThan("age", age)
                .whereEqualTo("favoritePetName", favoritePetName)
                .whereArrayContains("phoneNumbers", phoneNumber)
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

    public Mono<UserDocument> randomGenerate() {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(false);
        TransactionalOperator operator = TransactionalOperator.create(this.transactionManager, transactionDefinition);

        Random random = new Random();
        // Generate random user details
        var user = UserDocument.builder()
                .name(
                        UserName.builder()
                                .first("RandomUserNameFirst_" + random.nextInt(1000))
                                .last("RandomUserNameLast_" + random.nextInt(1000))
                                .build()
                )
                .favoritePetName("Pet_" + random.nextInt(100))
                .age(10 + random.nextInt(90)) // Random age between 10 and 99
                .phoneNumbers(List.of(String.valueOf(random.nextInt(10000000) + 90000000))) // Random phone number
                .build();

        // Generate a random number of roles (1 to 5) with random names
        var rolesFlux = Flux.fromIterable(
                IntStream.rangeClosed(1, 1 + random.nextInt(5))
                        .mapToObj(i -> RoleDocument.builder().name("Role_" + random.nextInt(100)).build())
                        .collect(Collectors.toList())
        );

        return repository.save(user)
                .flatMap(savedUser -> template.withParent(savedUser).saveAll(rolesFlux)
                        .then(Mono.just(savedUser))
                ).as(operator::transactional);
    }

    public Flux<UserDocument> getAllUsers() {
        var future = firestore.collection("users")
                .orderBy("name.first")
                .orderBy("age")
                .get();
        return ApiFutureUtil.toMono(future)
                .map(it -> it.toObjects(UserDocument.class))
                .flatMapMany(Flux::fromIterable);
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


    public Mono<UserDocument> updateUser(UserDocument user) {
        return repository.save(user);
    }

    public Flux<Long> countRoles() {
        Query query = firestore.collectionGroup("roles");
        AggregateQuery countQuery = query.count();
        return ApiFutureUtil.toMono(countQuery.get())
                .map(AggregateQuerySnapshot::getCount)
                .flux();
    }
}
