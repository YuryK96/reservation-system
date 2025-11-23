package com.example.reservation.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception e
    ) {
        log.error("Handle Exception: ", e);

        ErrorResponseDto errorDto = new ErrorResponseDto(
                "Internal server error",
                new String[]{e.getMessage()},
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(
            EntityNotFoundException e
    ) {
        log.error("Handle handleEntityNotFoundException: ", e);

        ErrorResponseDto errorDto = new ErrorResponseDto(
                        "Not Found",
                new String[]{e.getMessage()},
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(
            Exception e
    ) {
        log.error("Handle handleBadRequestException: ", e);

        String[] message;

        if(e instanceof MethodArgumentNotValidException) {
            List<ObjectError> errors = ((MethodArgumentNotValidException) e).getAllErrors();

            message = errors.stream()
                    .map(error -> {
                        if (error instanceof FieldError fieldError) {
                            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                        } else {
                            return error.getDefaultMessage();
                        }
                    })
                    .toArray(String[]::new);

        } else {
            message = new String[]{e.getMessage()};
        }

        ErrorResponseDto errorDto = new ErrorResponseDto(
                "Illegal arguments",
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }
}
