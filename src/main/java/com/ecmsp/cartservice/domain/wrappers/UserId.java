package com.ecmsp.cartservice.domain.wrappers;

import lombok.Data;

@Data
public class UserId {
    private final long UserId;

    public UserId(long userId) {
        UserId = userId;
    }
}
