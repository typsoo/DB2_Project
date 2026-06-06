package org.internetstore.scootersrentapplication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleBusinessErrors(IllegalStateException ex) {
        ErrorResponse error = new ErrorResponse("BUSINESS_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error); // 400
    }

//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<ErrorResponse> handleStateErrors(IllegalArgumentException ex) {
//        ErrorResponse error = new ErrorResponse("BUSINESS_ERROR", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error); // 400
//    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseErrors(DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse("DATABASE_ERROR", "Data save failure. Incorrect database status or restriction.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error); // 409
    }

    public record ErrorResponse(String errorCode, String message) {}
}