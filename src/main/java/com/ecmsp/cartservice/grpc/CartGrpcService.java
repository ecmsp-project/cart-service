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
    public void subtractProduct(SubtractProductRequest request, StreamObserver<SubtractProductResponse> responseObserver) {
        try {
        UserContextData userContextData = UserContextGrpcHolder.getUserContext();
        UserId userId = UserId.fromString(userContextData.userId());

        CartProductDto cartProductDto = cartGrpcMapper.toCartProductDto(request);
        CartDto updatedCart = cartService.subtractProductQuantity(userId, cartProductDto);
        SubtractProductResponse response = cartGrpcMapper.toSubtractProductResponse(updatedCart);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        }catch (Exception e){
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void updateQuantity(UpdateQuantityRequest request, StreamObserver<UpdateQuantityResponse> responseObserver) {
        try{
            UserContextData userContextData = UserContextGrpcHolder.getUserContext();
            UserId userId = UserId.fromString(userContextData.userId());

            CartProductDto cartProductDto = cartGrpcMapper.toCartProductDto(request);

            CartDto updatedCart = cartService.updateQuantityOfProduct(userId, cartProductDto);
            UpdateQuantityResponse response = cartGrpcMapper.toUpdateQuantityResponse(updatedCart);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch (Exception e){
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

}
