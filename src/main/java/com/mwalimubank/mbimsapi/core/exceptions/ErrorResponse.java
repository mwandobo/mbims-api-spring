package com.mwalimubank.mbimsapi.core.exceptions;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {}