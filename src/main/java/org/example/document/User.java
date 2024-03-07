package org.example.document;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collectionName = "users")
public class User {
    @DocumentId
    private String documentId;
    @PropertyName("name")
    private String name;
    @PropertyName("age")
    private Integer age;
}
