package com.practice.CommerceX.dto;

public class LoginResponse {

//	private String token;
	private String accessToken;
    private String refreshToken;
	 public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public LoginResponse(
            String accessToken,
            String refreshToken) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }


//	public LoginResponse(String token) {
//	        this.token = token;
//	    }
//
//	public String getToken() {
//		return token;
//	}
//
//	public void setToken(String token) {
//		this.token = token;
//	}
	
}
