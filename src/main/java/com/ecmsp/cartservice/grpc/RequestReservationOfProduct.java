package com.ecmsp.cartservice.grpc;

import com.ecmsp.cartservice.dto.ReservationMessageResponse;
import com.ecmsp.cartservice.dto.ReservationProductMessage;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestReservationOfProduct {

//    @GrpcClient("product-service")
//    private ProductReservationServiceGrpc.ProductReservationServiceBlockingStub productReservationStub;
//
//    public ReservationMessageResponse reserveProducts(ReservationProductMessage message) {
//        try {
//            log.info("Attempting to reserve products via gRPC: {}", message.products());
//
//            // Build gRPC request from internal DTO
//            ReserveProductsRequest grpcRequest = ReserveProductsRequest.newBuilder()
//                    .addAllItems(message.products().stream()
//                            .map(product -> ProductReservationItem.newBuilder()
//                                    .setProductId(product.productId())
//                                    .setQuantity(product.quantity())
//                                    .build())
//                            .collect(Collectors.toList()))
//                    .build();
//
//            // Make gRPC call to product-service
//            ReserveProductsResponse grpcResponse = productReservationStub.reserveProducts(grpcRequest);
//
//            log.info("Received gRPC response: success={}, message={}, reserved_variants={}, failed_reservations={}",
//                    grpcResponse.getSuccess(),
//                    grpcResponse.getMessage(),
//                    grpcResponse.getReservedVariantIdsList(),
//                    grpcResponse.getFailedReservationsCount());
//
//            // Convert gRPC response to internal DTO
//            return new ReservationMessageResponse(
//                    grpcResponse.getSuccess(),
//                    grpcResponse.getReservedVariantIdsList()
//            );
//
//        } catch (StatusRuntimeException e) {
//            log.error("gRPC call failed with status: {}, description: {}", e.getStatus(), e.getStatus().getDescription());
//            return new ReservationMessageResponse(false, List.of());
//        } catch (Exception e) {
//            log.error("Unexpected error during product reservation", e);
//            return new ReservationMessageResponse(false, List.of());
//        }
//    }
}
