
package com.file_storage.file_storage.validation;

import com.file_storage.file_storage.exceptions.InvalidRequestException;

import org.springframework.stereotype.Component;

@Component
public class InputValidator {

    private static final String USERNAME_PATTERN =
            "^[A-Za-z0-9._-]{1,64}$";

    // 1. Validate username
    public void validateUserName(String userName) {

        if (userName == null || userName.isBlank()) {
            throw new InvalidRequestException(
                    "Username cannot be empty"
            );
        }

        if (!userName.matches(USERNAME_PATTERN)
                || userName.equals(".")
                || userName.equals("..")) {

            throw new InvalidRequestException(
                    "Invalid username. Username must contain only "
                            + "letters, numbers, dots, underscores "
                            + "or hyphens and must be 1-64 characters long."
            );
        }
    }

    // 2. Validate relative file path
    public void validatePath(String path) {

        if (path == null || path.isBlank()) {
            throw new InvalidRequestException(
                    "File path cannot be empty"
            );
        }

        // Paths must not start or end with /
        if (path.startsWith("/") || path.endsWith("/")) {
            throw new InvalidRequestException(
                    "File path must not start or end with /"
            );
        }

        // Backslashes are not allowed
        if (path.contains("\\")) {
            throw new InvalidRequestException(
                    "Backslashes are not allowed in file paths"
            );
        }

        // Check individual path segments
        String[] segments = path.split("/", -1);

        for (String segment : segments) {

            if (segment.isBlank()
                    || segment.equals(".")
                    || segment.equals("..")) {

                throw new InvalidRequestException(
                        "Invalid file path segment: " + segment
                );
            }
        }
    }

    // 3. Sanitize uploaded filename
    public String sanitizeFileName(String originalFileName) {

        if (originalFileName == null
                || originalFileName.isBlank()) {

            throw new InvalidRequestException(
                    "Uploaded filename cannot be empty"
            );
        }

        // Remove directory information from the filename
        String fileName = originalFileName.replace("\\", "/");

        fileName = fileName.substring(
                fileName.lastIndexOf("/") + 1
        );

        // Validate the extracted filename
        validatePath(fileName);

        return fileName;
    }
}
