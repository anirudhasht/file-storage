
package com.file_storage.file_storage.storage;

import com.file_storage.file_storage.model.FilePage;
import com.file_storage.file_storage.exceptions.StorageException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    private S3StorageService s3StorageService;

    private static final String BUCKET = "test-bucket";

    @BeforeEach
    void setUp() {
        s3StorageService = new S3StorageService(s3Client, BUCKET);
    }

    @Test
    void shouldListFilesForUser() {
        S3Object object = S3Object.builder()
                .key("sandy/logistics_report.txt")
                .size(72L)
                .lastModified(Instant.now())
                .build();

        ListObjectsV2Response s3Response =
                ListObjectsV2Response.builder()
                        .contents(List.of(object))
                        .isTruncated(false)
                        .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(s3Response);

        FilePage result = s3StorageService.listPage(
                "sandy", null, null);

        assertEquals(1, result.files().size());
        assertEquals(
                "logistics_report.txt",
                result.files().get(0).path()
        );
        assertNull(result.nextToken());
    }

    @Test
    void shouldCheckWhetherUserFolderExists() {
        S3Object object = S3Object.builder()
                .key("sandy/logistics_report.txt")
                .size(72L)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(ListObjectsV2Response.builder()
                        .contents(List.of(object))
                        .build());

        assertTrue(s3StorageService.userFolderExists("sandy"));
    }

    @Test
    void shouldReturnFalseWhenUserFolderDoesNotExist() {
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(ListObjectsV2Response.builder()
                        .contents(List.of())
                        .build());

        assertFalse(s3StorageService.userFolderExists("unknown"));
    }

    @Test
    void shouldUploadFileToS3() {
        byte[] content = "test content".getBytes();

        assertDoesNotThrow(() ->
                s3StorageService.upload(
                        "sandy",
                        "reports/test.txt",
                        new ByteArrayInputStream(content),
                        content.length,
                        "text/plain"
                )
        );

        verify(s3Client).putObject(
                any(PutObjectRequest.class),
                any(software.amazon.awssdk.core.sync.RequestBody.class)
        );
    }
}
