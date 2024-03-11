package org.example.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.example.document.UserDocument;
import reactor.core.publisher.Flux;

public interface UserRepository extends FirestoreReactiveRepository<UserDocument> {

    Flux<UserDocument> findByAge(Integer age);
}
