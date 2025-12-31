package com.ecmsp.cartservice.grpc.context;

import io.grpc.Context;

public class UserContextGrpcHolder {

    public static final Context.Key<UserContextData> USER_CONTEXT_KEY = Context.key("user-context");

    public static UserContextData getUserContext() {
        return USER_CONTEXT_KEY.get();
    }
}
