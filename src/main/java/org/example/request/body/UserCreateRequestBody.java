package org.example.request.body;

import org.example.document.UserName;

import java.util.List;

public record UserCreateRequestBody(
        UserName name,
        Integer age,
        List<String> phoneNumbers,
        List<RoleRequestBody> roles
) {
}
