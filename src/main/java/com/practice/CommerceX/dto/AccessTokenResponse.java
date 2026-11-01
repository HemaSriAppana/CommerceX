package com.practice.CommerceX.dto;

public class AccessTokenResponse {

    private String accessToken;

    public AccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}