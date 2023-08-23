package com.example.demo.repository.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ProjectRequestDto(

        @NotNull(message = "owner can't be null")
        @NotEmpty(message = "owner can't be empty")
        String owner,
        @NotNull(message = "name can't be nul")
        @NotEmpty(message = "name can't be empty")
        String name) {

}
