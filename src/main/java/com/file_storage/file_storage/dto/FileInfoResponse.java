
package com.file_storage.file_storage.dto;

import java.time.Instant;

public class FileInfoResponse {

    private final String name;
    private final String path;
    private final long size;
    private final Instant lastModified;

    public FileInfoResponse(
            String name,
            String path,
            long size,
            Instant lastModified
    ) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public Instant getLastModified() {
        return lastModified;
    }
}
