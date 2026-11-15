
package com.file_storage.file_storage.dto;

import java.util.List;

public class SearchResponse {

    private final String userName;
    private final String searchTerm;
    private final int count;
    private final List<FileInfoResponse> files;
    private final String nextCursor;

    public SearchResponse(
            String userName,
            String searchTerm,
            int count,
            List<FileInfoResponse> files,
            String nextCursor
    ) {
        this.userName = userName;
        this.searchTerm = searchTerm;
        this.count = count;
        this.files = List.copyOf(files);
        this.nextCursor = nextCursor;
    }

    public String getUserName() {
        return userName;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public int getCount() {
        return count;
    }

    public List<FileInfoResponse> getFiles() {
        return files;
    }

    public String getNextCursor() {
        return nextCursor;
    }
}
