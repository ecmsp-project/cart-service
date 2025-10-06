package com.ecmsp.cartservice.jwt;

import com.ecmsp.cartservice.domain.wrappers.UserId;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {
    UserId extractUserIdFromRequest(HttpServletRequest request);
}