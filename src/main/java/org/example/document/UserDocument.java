package org.example.document;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collectionName = "users")
public class UserDocument {
    @DocumentId
    private String documentId;
    @PropertyName("name")
    private UserName name;
    @PropertyName("name")
    private String favoritePetName;
    @PropertyName("age")
    private Integer age;
    @PropertyName("phoneNumbers")
    private List<String> phoneNumbers;
}
