
package com.file_storage.file_storage.controller;

import com.file_storage.file_storage.dto.FileInfoResponse;
import com.file_storage.file_storage.model.StoredObject;
import com.file_storage.file_storage.service.FileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private FileService fileService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        FileController controller = new FileController(fileService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldSearchFiles() throws Exception {
        var response = new com.file_storage.file_storage.dto.SearchResponse(
                "sandy",
                "logistics",
                0,
                java.util.List.of(),
                null
        );

        when(fileService.searchFiles("sandy", "logistics", 50, null))
                .thenReturn(response);

        mockMvc.perform(get("/api/users/sandy/files")
                        .param("search", "logistics")
                        .param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("sandy"))
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    void shouldUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.txt",
                "text/plain",
                "test content".getBytes()
        );

        FileInfoResponse response = new FileInfoResponse(
                "report.txt",
                "reports/report.txt",
                12L,
                Instant.now()
        );

        when(fileService.uploadFile(
                eq("sandy"),
                eq("reports"),
                eq("report.txt"),
                any(),
                eq(12L),
                eq("text/plain")
        )).thenReturn(response);

        mockMvc.perform(multipart("/api/users/sandy/files")
                        .file(file)
                        .param("folder", "reports"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("report.txt"))
                .andExpect(jsonPath("$.path").value("reports/report.txt"));
    }

    @Test
    void shouldDownloadFile() throws Exception {
        byte[] content = "hello from s3".getBytes();

        StoredObject storedObject = new StoredObject(
                new ByteArrayInputStream(content),
                content.length,
                "text/plain"
        );

        when(fileService.downloadFile("sandy", "report.txt"))
                .thenReturn(storedObject);

        mockMvc.perform(get("/api/users/sandy/files/download")
                        .param("path", "report.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"))
                .andExpect(content().bytes(content))
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"report.txt\""
                ));
    }
}
