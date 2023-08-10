package com.example.demo.repository.exceptions;

import com.example.demo.repository.RepositoryController;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice(assignableTypes = RepositoryController.class)
public class ApiValidationErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiValidationResponseDto handleValidationException(MethodArgumentNotValidException exception) {
        List<String> errors = getErrorsFromException(exception);
        return new ApiValidationResponseDto(errors, HttpStatus.BAD_REQUEST);
    }
public List<String> getErrorsFromException(MethodArgumentNotValidException exception){
        return  exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
}
}
