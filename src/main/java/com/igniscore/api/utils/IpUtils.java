package com.igniscore.api.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {
    public static String getClientIp(HttpServletRequest request) {

        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()
                && !"unknown".equalsIgnoreCase(xForwardedFor)) {

            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");

        if (xRealIp != null && !xRealIp.isEmpty()
                && !"unknown".equalsIgnoreCase(xRealIp)) {

            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
