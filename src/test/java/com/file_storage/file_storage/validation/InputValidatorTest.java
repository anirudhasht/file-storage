
package com.file_storage.file_storage.validation;

import com.file_storage.file_storage.exceptions.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    private InputValidator validator;

    @BeforeEach
    void setUp() {
        validator = new InputValidator();
    }

    @Test
    void shouldAcceptValidUserName() {
        assertDoesNotThrow(() -> validator.validateUserName("sandy"));
    }

    @Test
    void shouldRejectInvalidUserName() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validateUserName("../admin"));
    }

    @Test
    void shouldRejectBlankUserName() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validateUserName(" "));
    }

    @Test
    void shouldAcceptValidNestedPath() {
        assertDoesNotThrow(
                () -> validator.validatePath("reports/2026/file.txt"));
    }

    @Test
    void shouldRejectPathTraversal() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validatePath("../secret.txt"));
    }

    @Test
    void shouldRejectAbsolutePath() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validatePath("/secret.txt"));
    }

    @Test
    void shouldSanitizeFileName() {
        String result = validator.sanitizeFileName(
                "C:\\Users\\Sandy\\report.pdf");

        assertEquals("report.pdf", result);
    }
}
