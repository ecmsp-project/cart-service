package com.ecmsp.cartservice.domain.wrappers;

import lombok.Data;

import java.util.UUID;

@Data
public class UserId {
    private final UUID userId;

    public UserId(UUID userId) {
        this.userId = userId;
    }

    public static UserId fromString(String userId) {
        return new UserId(UUID.fromString(userId));
    }
}
