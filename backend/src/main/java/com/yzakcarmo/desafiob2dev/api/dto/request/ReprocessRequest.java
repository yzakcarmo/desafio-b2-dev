package com.yzakcarmo.desafiob2dev.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReprocessRequest(
        @NotBlank String queue,
        @Min(1) @Max(100) int maxMessages
) {}