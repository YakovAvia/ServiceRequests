package com.rces.requestservice.bids.exception;


import com.rces.requestservice.bids.domain.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleException(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(new ErrorDto(ex.getStatusCode().value(), "Validation Failed", errors));

    }

    @ExceptionHandler(NotFoundOrderException.class)
    public ResponseEntity<ErrorDto> handleException(NotFoundOrderException ex) {

        ErrorDto error = new ErrorDto(404, "Order not found", ex.getMessage());

        return ResponseEntity.badRequest().body(error);

    }

    @ExceptionHandler(ValidateBidException.class)
    public ResponseEntity<ErrorDto> handleException(ValidateBidException ex) {

        ErrorDto error = new ErrorDto(500, "Нарушение валидации", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NotFoundBidItemException.class)
    public ResponseEntity<ErrorDto> handleException(NotFoundBidItemException ex) {

        ErrorDto error = new ErrorDto(404, "Item not found", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

}
