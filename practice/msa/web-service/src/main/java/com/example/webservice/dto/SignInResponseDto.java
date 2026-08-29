package com.example.webservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInResponseDto {
    private boolean loggedIn;
    private String url;
    private String userName;
    private String userId;
    private String message;
    private String accessToken;
    private String refreshToken;
}
