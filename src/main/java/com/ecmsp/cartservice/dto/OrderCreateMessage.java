package com.ecmsp.cartservice.dto;

import com.ecmsp.cartservice.dto.event.CartCreatedEvent;

public record OrderCreateMessage(CartCreatedEvent cartCreatedEvent) {
}
