package com.mwalimubank.mbimsapi.features.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class RegisterResponse {
    private String email;
    private String name;

}
