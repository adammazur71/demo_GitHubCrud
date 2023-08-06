package com.example.demo.repository.exceptions;

import com.example.demo.repository.RepositoryController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = RepositoryController.class)
public class IdErrorHandler {
    @ExceptionHandler(IdNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public IdNotFoundDto handleException(IdNotFoundException exception){
        return new IdNotFoundDto(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
