package com.example.demo.repository.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ApiValidationResponseDto(List<String> errors, HttpStatus status) {
}
