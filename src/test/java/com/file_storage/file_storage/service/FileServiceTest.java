
package com.file_storage.file_storage.service;

import com.file_storage.file_storage.exceptions.InvalidRequestException;
import com.file_storage.file_storage.model.FilePage;
import com.file_storage.file_storage.model.StoredFile;
import com.file_storage.file_storage.storage.StorageService;
import com.file_storage.file_storage.validation.InputValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private StorageService storageService;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService(
                storageService,
                new InputValidator()
        );
    }

    @Test
    void shouldFindMatchingFiles() {

        StoredFile file = new StoredFile(
                "logistics_report.txt",
                72L,
                Instant.now()
        );

        when(storageService.listPage("sandy", null, null))
                .thenReturn(new FilePage(List.of(file), null));

        var response = fileService.searchFiles(
                "sandy", "logistics", 50, null
        );

        assertEquals(1, response.getCount());
        assertEquals("logistics_report.txt",
                response.getFiles().get(0).getName());

        verify(storageService).listPage("sandy", null, null);
    }

    @Test
    void shouldSearchCaseInsensitively() {

        StoredFile file = new StoredFile(
                "Logistics_Report.txt",
                72L,
                Instant.now()
        );

        when(storageService.listPage("sandy", null, null))
                .thenReturn(new FilePage(List.of(file), null));

        var response = fileService.searchFiles(
                "sandy", "LOGISTICS", 50, null
        );

        assertEquals(1, response.getCount());
    }

    @Test
    void shouldReturnEmptyListWhenNoFilesMatch() {

        when(storageService.listPage("sandy", null, null))
                .thenReturn(new FilePage(List.of(), null));

        when(storageService.userFolderExists("sandy"))
                .thenReturn(true);

        var response = fileService.searchFiles(
                "sandy", "missing", 50, null
        );

        assertEquals(0, response.getCount());
        assertTrue(response.getFiles().isEmpty());
    }

    @Test
    void shouldRejectInvalidUserName() {

        assertThrows(
                InvalidRequestException.class,
                () -> fileService.searchFiles(
                        "../admin", "test", 50, null
                )
        );

        verifyNoInteractions(storageService);
    }
}
