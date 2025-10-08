package com.aipiabackend.auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
    @JsonProperty("access-token")
    String accessToken
) {
}