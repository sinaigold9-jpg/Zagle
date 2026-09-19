package com.zajel.app.data;

import java.io.File;

/** Provider-neutral contract for persisting media outside the database. */
public interface MediaStorageRepository {
    StoredMedia upload(String bearerToken, File file, String path, String mimeType) throws Exception;

    final class StoredMedia {
        public final String path;
        public final long sizeBytes;

        public StoredMedia(String path, long sizeBytes) {
            this.path = path;
            this.sizeBytes = sizeBytes;
        }
    }
}
