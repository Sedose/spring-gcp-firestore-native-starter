package org.example.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserName {
    private String first;
    private String last;

    @Override
    public String toString() {
        return "UserName{" +
                "first='" + first + '\'' +
                ", last='" + last + '\'' +
                '}';
    }
}
