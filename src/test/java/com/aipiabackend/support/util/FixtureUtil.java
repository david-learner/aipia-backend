package com.aipiabackend.support.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FixtureUtil {

    /**
     * Location 헤더에서 리소스 ID 추출한다
     *
     * @param location /resources/1 형식의 리소스 경로
     * @return 리소스 ID
     */
    public static Long getResourceIdFromLocation(String location) {
        String[] segments = location.split("/");
        return Long.valueOf(segments[segments.length - 1]);
    }

    public static String getJsonFrom(ObjectMapper objectMapper, Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
}
