package com.ecmsp.cartservice.grpc;

import com.ecmsp.cart.v1.*;
import com.ecmsp.cartservice.domain.Cart;
import com.ecmsp.cartservice.domain.wrappers.UserId;
import com.ecmsp.cartservice.dto.CartDto;
import com.ecmsp.cartservice.dto.CartProductDto;
import com.ecmsp.cartservice.grpc.context.UserContextData;
import com.ecmsp.cartservice.grpc.context.UserContextGrpcHolder;
import com.ecmsp.cartservice.service.CartService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class CartGrpcService extends CartServiceGrpc.CartServiceImplBase {
    private final CartService cartService;
    private final CartGrpcMapper cartGrpcMapper;

    public CartGrpcService(CartService cartService, CartGrpcMapper cartGrpcMapper) {
        this.cartService = cartService;
        this.cartGrpcMapper = cartGrpcMapper;
    }

    @Override
    public void getCart(GetCartRequest request, StreamObserver<GetCartResponse> responseObserver) {
        try {
            UserContextData userContextData = UserContextGrpcHolder.getUserContext();

            UserId userId = UserId.fromString(userContextData.userId());
            Cart cart = cartService.getCartOrCreateNew(userId);
            CartDto cartDto = cartService.convertCartToDTO(cart);

            GetCartResponse response = cartGrpcMapper.toGetCartResponse(cartDto);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            System.out.println("Internal error " + e.getMessage());
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void addProduct(AddProductRequest request, StreamObserver<AddProductResponse> responseObserver) {
        System.out.println("Add product");
        try {
            UserContextData userContextData = UserContextGrpcHolder.getUserContext();

            UserId userId = UserId.fromString(userContextData.userId());
            CartProductDto productDto = cartGrpcMapper.toCartProductDto(request.getProduct());

            CartDto updatedCart = cartService.addProductToCart(userId, productDto);
            AddProductResponse response = cartGrpcMapper.toAddProductResponse(updatedCart);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        try {
            UserContextData userContextData = UserContextGrpcHolder.getUserContext();
            UserId userId = UserId.fromString(userContextData.userId());

            CartProductDto productDto = cartGrpcMapper.toCartProductDto(request);

            CartDto updatedCart = cartService.deleteProductFromCart(userId, productDto);
            DeleteProductResponse response = cartGrpcMapper.toDeleteProductResponse(updatedCart);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteCart(DeleteCartRequest request, StreamObserver<DeleteCartResponse> responseObserver) {
        try {
            UserContextData userContextData = UserContextGrpcHolder.getUserContext();
            UserId userId = UserId.fromString(userContextData.userId());

            cartService.deleteCart(userId);

            DeleteCartResponse response = cartGrpcMapper.toDeleteCartResponse();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateQuantities(UpdateQuantitiesRequest request, StreamObserver<UpdateQuantitiesResponse> responseObserver) {
        try {
            UserContextData userContextData = UserContextGrpcHolder.getUserContext();
            UserId userId = UserId.fromString(userContextData.userId());

            CartDto cartDto = cartGrpcMapper.toCartDto(request);

            CartDto updatedCart = cartService.updateCart(userId, cartDto);
            UpdateQuantitiesResponse response = cartGrpcMapper.toUpdateQuantitiesResponse(updatedCart);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /**
     * @deprecated This method is deprecated and will be removed in a future version.
     * Order creation should be done by OrderService.
     */
    @Deprecated
    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<CreateOrderResponse> responseObserver) {
        try {
            // TODO: Implement order creation logic via Kafka or REST
            // For now, return a placeholder response
            CreateOrderResponse response = cartGrpcMapper.toCreateOrderResponse("placeholder-order-id");
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
