package com.mwalimubank.mbimsapi.features.auth.dtos;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String otp;
}