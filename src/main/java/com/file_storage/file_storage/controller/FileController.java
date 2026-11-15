
package com.file_storage.file_storage.controller;

import com.file_storage.file_storage.dto.FileInfoResponse;
import com.file_storage.file_storage.dto.SearchResponse;
import com.file_storage.file_storage.model.StoredObject;
import com.file_storage.file_storage.service.FileService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users/{userName}/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // Searching files
    @GetMapping
    public ResponseEntity<SearchResponse> searchFiles( @PathVariable String userName, @RequestParam(required = false) String search, @RequestParam(defaultValue = "50") int limit,
     @RequestParam(required = false) String cursor
    ) 
    {
        return ResponseEntity.ok(
                fileService.searchFiles(userName, search, limit, cursor)
        );
    }

    // Downloading  file
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable String userName,
            @RequestParam String path
    ) {
        StoredObject file = fileService.downloadFile(userName, path);

        String fileName = path.substring(path.lastIndexOf('/') + 1);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .contentType(
                        file.contentType() != null
                                ? MediaType.parseMediaType(file.contentType())
                                : MediaType.APPLICATION_OCTET_STREAM
                )
                .contentLength(file.contentLength())
                .body(new InputStreamResource(file.content()));
    }

    // Uploading file
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileInfoResponse> uploadFile(
            @PathVariable String userName,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String folder
    ) throws IOException {

        FileInfoResponse response = fileService.uploadFile(
                userName,
                folder,
                file.getOriginalFilename(),
                file.getInputStream(),
                file.getSize(),
                file.getContentType()
        );

        return ResponseEntity.status(201).body(response);
    }
}
