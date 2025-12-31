package com.ecmsp.cartservice.grpc.context;

import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@GrpcGlobalServerInterceptor
public class UserContextGrpcInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        UserContextData userContextData = new UserContextData(
                headers.get(Metadata.Key.of("X-User-ID", Metadata.ASCII_STRING_MARSHALLER)),
                headers.get(Metadata.Key.of("X-Login", Metadata.ASCII_STRING_MARSHALLER))
        );

        Context context = Context.current()
                .withValue(UserContextGrpcHolder.USER_CONTEXT_KEY, userContextData);

        return Contexts.interceptCall(context, call, headers, next);
    }
}
