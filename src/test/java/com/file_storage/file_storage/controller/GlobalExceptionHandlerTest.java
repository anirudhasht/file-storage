
package com.file_storage.file_storage.controller;

import com.file_storage.file_storage.exceptions.InvalidRequestException;
import com.file_storage.file_storage.exceptions.ResourceNotFoundException;
import com.file_storage.file_storage.exceptions.StorageException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn400ForInvalidRequest() throws Exception {
        mockMvc.perform(get("/test/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void shouldReturn404ForMissingResource() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void shouldReturn502ForStorageFailure() throws Exception {
        mockMvc.perform(get("/test/storage-error"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/invalid")
        public void invalid() {
            throw new InvalidRequestException("Invalid input");
        }

        @GetMapping("/test/not-found")
        public void notFound() {
            throw new ResourceNotFoundException("File not found");
        }

        @GetMapping("/test/storage-error")
        public void storageError() {
            throw new StorageException(
                    "Storage operation failed",
                    new RuntimeException("S3 unavailable")
            );
        }
    }
}
