package com.example.demo.repository.exceptions;

import com.example.demo.repository.RepositoryController;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = RepositoryController.class)
@Log4j2
public class IdErrorHandler {
    @ExceptionHandler(IdNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public IdNotFoundDto handleException(IdNotFoundException exception){
        log.error(exception.getMessage() + " , HTTP STATUS: " + HttpStatus.NOT_FOUND);
        return new IdNotFoundDto(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
