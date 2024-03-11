package org.example.request.body;

import java.util.List;

public record UserCreateRequestBody(
        String name,
        Integer age,
        List<String> phoneNumbers,
        List<RoleRequestBody> roles
) {
}
