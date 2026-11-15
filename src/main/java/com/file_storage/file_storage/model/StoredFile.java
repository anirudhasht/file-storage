
package com.file_storage.file_storage.model;

import java.time.Instant;

public class StoredFile {

    private final String path;
    private final long size;
    private final Instant lastModified;

    public StoredFile(String path, long size, Instant lastModified) {
        this.path = path;
        this.size = size;
        this.lastModified = lastModified;
    }

    public String path() {
        return path;
    }

    public long size() {
        return size;
    }

    public Instant lastModified() {
        return lastModified;
    }

    public String name() {
        return nameOf(path);
    }

    public static String nameOf(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
