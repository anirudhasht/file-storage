
package com.file_storage.file_storage.model;

import java.util.List;

public class FilePage {

    private final List<StoredFile> files;
    private final String nextToken;

    public FilePage(List<StoredFile> files, String nextToken) {
        this.files = List.copyOf(files);
        this.nextToken = nextToken;
    }

    public List<StoredFile> files() {
        return files;
    }

    public String nextToken() {
        return nextToken;
    }
}
