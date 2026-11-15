
package com.file_storage.file_storage.service;

import com.file_storage.file_storage.dto.FileInfoResponse;
import com.file_storage.file_storage.dto.SearchResponse;
import com.file_storage.file_storage.exceptions.InvalidRequestException;
import com.file_storage.file_storage.exceptions.ResourceNotFoundException;
import com.file_storage.file_storage.model.FilePage;
import com.file_storage.file_storage.model.StoredFile;
import com.file_storage.file_storage.model.StoredObject;
import com.file_storage.file_storage.storage.StorageService;
import com.file_storage.file_storage.validation.InputValidator;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class FileService {

    private static final int MAX_LIMIT = 200;

    private final StorageService storageService;
    private final InputValidator inputValidator;

    public FileService(
            StorageService storageService,
            InputValidator inputValidator
    ) {
        this.storageService = storageService;
        this.inputValidator = inputValidator;
    }

  //searching a file in s3 bucket for particular user

    public SearchResponse searchFiles(
            String userName,
            String searchTerm,
            int limit,
            String cursor
    ) {

        // Validating username
        inputValidator.validateUserName(userName);

        // Validate limit
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new InvalidRequestException(
                    "Limit must be between 1 and 200"
            );
        }

        // Validating cursor if supplied
        if (cursor != null && !cursor.isBlank()) {
            inputValidator.validatePath(cursor);
        } else {
            cursor = null;
        }

        String term = searchTerm == null ? "" : searchTerm;

        List<StoredFile> matchedFiles = new ArrayList<>();

        String continuationToken = null;
        String startAfterPath = cursor;

        boolean hasMoreResults = false;

        while (true) {

            FilePage page = storageService.listPage(
                    userName,
                    startAfterPath,
                    continuationToken
            );

           
            startAfterPath = null;

            List<StoredFile> files = page.files();

            for (int i = 0; i < files.size(); i++) {

                StoredFile file = files.get(i);

                
                if (matches(file.name(), term)) {

                    matchedFiles.add(file);

                    
                    if (matchedFiles.size() == limit) {

                        boolean remainingInCurrentPage =
                                i < files.size() - 1;

                        boolean remainingS3Pages =
                                page.nextToken() != null;

                        hasMoreResults =
                                remainingInCurrentPage
                                        || remainingS3Pages;

                        String nextCursor = hasMoreResults
                                ? file.path()
                                : null;

                        return buildSearchResponse(
                                userName,
                                term,
                                matchedFiles,
                                nextCursor
                        );
                    }
                }
            }

            continuationToken = page.nextToken();

        
            if (continuationToken == null) {
                break;
            }
        }

        
        if (matchedFiles.isEmpty()
                && !storageService.userFolderExists(userName)) {

            throw new ResourceNotFoundException(
                    "User folder not found: " + userName
            );
        }

        return buildSearchResponse(
                userName,
                term,
                matchedFiles,
                null
        );
    }

   //filename matching


    private boolean matches(
            String fileName,
            String searchTerm
    ) {

        if (searchTerm.isBlank()) {
            return true;
        }

        return fileName.toLowerCase(Locale.ROOT)
                .contains(searchTerm.toLowerCase(Locale.ROOT));
    }

 

    private SearchResponse buildSearchResponse(
            String userName,
            String searchTerm,
            List<StoredFile> files,
            String nextCursor
    ) {

        List<FileInfoResponse> fileResponses = files.stream()
                .map(this::toFileInfoResponse)
                .toList();

        return new SearchResponse(
                userName,
                searchTerm,
                fileResponses.size(),
                fileResponses,
                nextCursor
        );
    }

    private FileInfoResponse toFileInfoResponse(
            StoredFile file
    ) {

        return new FileInfoResponse(
                file.name(),
                file.path(),
                file.size(),
                file.lastModified()
        );
    }
//downloading a file

    public StoredObject downloadFile(
            String userName,
            String path
    ) {

        inputValidator.validateUserName(userName);
        inputValidator.validatePath(path);

        return storageService.download(userName, path);
    }

   //uploading a file

    public FileInfoResponse uploadFile(
            String userName,
            String folder,
            String originalFileName,
            InputStream data,
            long size,
            String contentType
    ) {

        inputValidator.validateUserName(userName);

        if (data == null) {
            throw new InvalidRequestException(
                    "File content cannot be empty"
            );
        }

        if (size < 0) {
            throw new InvalidRequestException(
                    "Invalid file size"
            );
        }

        String fileName =
                inputValidator.sanitizeFileName(originalFileName);

        String path;

        if (folder == null || folder.isBlank()) {

            path = fileName;

        } else {

            inputValidator.validatePath(folder);

            path = folder + "/" + fileName;
        }

        
        storageService.upload(
                userName,
                path,
                data,
                size,
                contentType
        );

        return new FileInfoResponse(
                fileName,
                path,
                size,
                Instant.now()
        );
    }
}
