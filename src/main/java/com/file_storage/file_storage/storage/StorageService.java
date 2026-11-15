
package com.file_storage.file_storage.storage;

import com.file_storage.file_storage.model.FilePage;
import com.file_storage.file_storage.model.StoredObject;

import java.io.InputStream;

public interface StorageService {

    FilePage listPage(String userName,String startAfterPath,String continuationToken);

    boolean userFolderExists(String userName);

    StoredObject download(String userName,String path );

    void upload(String userName,String path,InputStream data,long size,String contentType);
}
