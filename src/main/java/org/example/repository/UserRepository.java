package org.example.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.example.document.User;
import reactor.core.publisher.Flux;

public interface UserRepository extends FirestoreReactiveRepository<User> {

    Flux<User> findByAge(Integer age);
}
