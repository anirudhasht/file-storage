
package com.file_storage.file_storage.controller;

import com.file_storage.file_storage.exceptions.ApiError;
import com.file_storage.file_storage.exceptions.InvalidRequestException;
import com.file_storage.file_storage.exceptions.ResourceNotFoundException;
import com.file_storage.file_storage.exceptions.StorageException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildErrorResponse(
            HttpStatus status,
            String message
    ) {

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(error);
    }

    // 400 - Invalid request
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiError> handleInvalidRequest(
            InvalidRequestException ex
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // 404 - Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex
    ) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    // 413 - Upload exceeds configured size
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSize(
            MaxUploadSizeExceededException ex
    ) {

        return buildErrorResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "Uploaded file exceeds the maximum allowed size"
        );
    }

    // 502 - AWS or storage failure
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiError> handleStorageException(
            StorageException ex
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_GATEWAY,
                "Storage operation failed"
        );
    }
}
