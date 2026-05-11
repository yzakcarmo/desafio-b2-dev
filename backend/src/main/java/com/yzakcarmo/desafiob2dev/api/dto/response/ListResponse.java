package com.yzakcarmo.desafiob2dev.api.dto.response;

import java.util.UUID;

public record ListResponse(
        String label,
        String value,
        UUID id
) {}
