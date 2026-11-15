
package com.file_storage.file_storage.model;

import java.io.InputStream;

public class StoredObject {

    private final InputStream content;
    private final long contentLength;
    private final String contentType;

    public StoredObject( InputStream content,long contentLength,String contentType ) {

        this.content = content;
        this.contentLength = contentLength;
        this.contentType = contentType;
    }

    public InputStream content() {
        return content;
    }

    public long contentLength() {
        return contentLength;
    }

    public String contentType() {
        return contentType;
    }
}
