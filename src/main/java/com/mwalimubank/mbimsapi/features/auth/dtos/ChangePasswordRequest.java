package com.mwalimubank.mbimsapi.features.auth.dtos;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}