package com.ecmsp.cartservice.dto;

import java.util.List;

public record OrderCreateMessage(List<String> variants) {
}
