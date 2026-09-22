package com.mwalimubank.mbimsapi.features.auth.dtos;

import lombok.Data;

@Data
public class RecoverPasswordRequest {
    private String email;
}