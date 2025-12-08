package com.ecmsp.cartservice.util;

import java.util.UUID;

/**
 * Utility for converting between external Integer product IDs and internal UUID representation.
 * Uses a stable encoding where the UUID is 00000000-0000-0000-0000-XXXXXXXXXXXX (hex of the integer, 12 chars).
 */
public final class ProductIdMapper {
    private ProductIdMapper() {}

    public static UUID intToUuid(Integer productId) {
        if (productId == null) return null;
        String lastSegment = String.format("%012x", productId);
        String uuidStr = "00000000-0000-0000-0000-" + lastSegment;
        return UUID.fromString(uuidStr);
    }

    public static Integer uuidToInt(UUID uuid) {
        if (uuid == null) return null;
        String s = uuid.toString();
        String last = s.substring(s.lastIndexOf('-') + 1);
        // parse as hex back to integer
        long val = Long.parseLong(last, 16);
        return (int) val;
    }
}
