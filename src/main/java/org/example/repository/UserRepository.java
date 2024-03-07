package org.example.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.example.document.User;

public interface UserRepository extends FirestoreReactiveRepository<User> {
}
