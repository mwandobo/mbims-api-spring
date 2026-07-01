package com.mwalimubank.mbimsapi.features.auth.dtos;

import lombok.Data;

@Data
public class PasswordRecoveryRequest {
    private String email;
    private String password;
}