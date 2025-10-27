package com.ecmsp.cartservice.jwt;

import com.ecmsp.cartservice.domain.wrappers.UserId;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockJwtService implements JwtService {

    @Override
    public UserId extractUserIdFromRequest(HttpServletRequest request) {
        // Mock implementation - returns hardcoded userId
        // In real implementation, this would extract JWT token from Authorization header
        // and decode it to get the actual user ID
        log.debug("MockJwtService: returning hardcoded UserId(1)");
        return new UserId(1L);
    }
}