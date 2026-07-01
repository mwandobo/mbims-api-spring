package com.mwalimubank.mbimsapi.features.auth.dtos;

import com.mwalimubank.mbimsapi.features.notification.dto.NotificationResponseDto;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import com.mwalimubank.mbimsapi.features.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponse {

    private String access_token;
    private UserResponseDTO user;
    private List<NotificationResponseDto> notifications;
}