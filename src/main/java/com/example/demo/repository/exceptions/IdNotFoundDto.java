package com.example.demo.repository.exceptions;

import org.springframework.http.HttpStatus;

public record IdNotFoundDto(String message, HttpStatus status) {
}
