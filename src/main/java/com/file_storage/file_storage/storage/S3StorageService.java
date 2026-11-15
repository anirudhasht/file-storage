
package com.file_storage.file_storage.storage;

import com.file_storage.file_storage.exceptions.ResourceNotFoundException;
import com.file_storage.file_storage.exceptions.StorageException;
import com.file_storage.file_storage.model.FilePage;
import com.file_storage.file_storage.model.StoredFile;
import com.file_storage.file_storage.model.StoredObject;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.exception.SdkException;

import java.io.InputStream;
import java.util.List;

@Service
public class S3StorageService implements StorageService {

    private static final int MAX_KEYS = 1000;

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    //Lists the files belonging to a particular user
    @Override
    public FilePage listPage(String userName,String startAfterPath,String continuationToken) {

        String prefix = userName + "/";

        try {

            ListObjectsV2Request.Builder requestBuilder =
                    ListObjectsV2Request.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .maxKeys(MAX_KEYS);

            
            if (continuationToken != null
                    && !continuationToken.isBlank()) {

                requestBuilder.continuationToken(continuationToken);

            } else if (startAfterPath != null
                    && !startAfterPath.isBlank()) {

                requestBuilder.startAfter(prefix + startAfterPath);
            }

            ListObjectsV2Response response =
                    s3Client.listObjectsV2(requestBuilder.build());

            List<StoredFile> files = response.contents()
                    .stream()
                   
                    .filter(object -> !object.key().endsWith("/"))
                    
                    .map(object -> toStoredFile(object, prefix))
                    .toList();

            String nextToken = Boolean.TRUE.equals(response.isTruncated())
                    ? response.nextContinuationToken()
                    : null;

            return new FilePage(files, nextToken);

        } catch (SdkException e) {

            throw new StorageException(
                    "Failed to list files for user: " + userName,
                    e
            );
        }
    }

    //  Check whether a user's folder already exists
    @Override
    public boolean userFolderExists(String userName) {

        String prefix = userName + "/";

        try {

            ListObjectsV2Request request =
                    ListObjectsV2Request.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .maxKeys(1)
                            .build();

            ListObjectsV2Response response =
                    s3Client.listObjectsV2(request);

            return !response.contents().isEmpty();

        } catch (SdkException e) {

            throw new StorageException(
                    "Failed to check user folder: " + userName,
                    e
            );
        }
    }

    //  Downloading  a file
    @Override
    public StoredObject download( String userName, String path ) {

        String key = userName + "/" + path;

        try {

            GetObjectRequest request =
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

            ResponseInputStream<GetObjectResponse> response =
                    s3Client.getObject(request);

            GetObjectResponse metadata = response.response();

            return new StoredObject(
                    response,
                    metadata.contentLength(),
                    metadata.contentType()
            );

        } catch (NoSuchKeyException e) {

            throw new ResourceNotFoundException(
                    "File not found: " + path
            );

        } catch (S3Exception e) {

            if (e.statusCode() == 404) {
                throw new ResourceNotFoundException(
                        "File not found: " + path
                );
            }

            throw new StorageException(
                    "Failed to download file: " + path,
                    e
            );

        } catch (SdkException e) {

            throw new StorageException(
                    "Failed to download file: " + path,
                    e
            );
        }
    }

    // Uploading  a file
    @Override
    public void upload(
            String userName,
            String path,
            InputStream data,
            long size,
            String contentType
    ) {

        String key = userName + "/" + path;

        try {

            PutObjectRequest request =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(
                                    contentType != null
                                            ? contentType
                                            : "application/octet-stream"
                            )
                            .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(data, size)
            );

        } catch (SdkException | IllegalArgumentException e) {

            throw new StorageException(
                    "Failed to upload file: " + path,
                    e
            );
        }
    }

   
    private StoredFile toStoredFile(S3Object object,String prefix ) {

        String relativePath =
                object.key().substring(prefix.length());

        return new StoredFile(
                relativePath,
                object.size(),
                object.lastModified()
        );
    }
}
