package com.zajel.app.data;

import com.zajel.app.core.SupabaseClient;
import java.io.File;

/** Initial storage adapter; the chat layer depends only on MediaStorageRepository. */
public final class SupabaseMediaStorageRepository implements MediaStorageRepository {
    private final SupabaseClient api;

    public SupabaseMediaStorageRepository(SupabaseClient api) {
        this.api = api;
    }

    @Override public StoredMedia upload(String bearerToken, File file, String path, String mimeType) throws Exception {
        api.upload("/storage/v1/object/zajel-media/" + path, bearerToken, file, mimeType);
        return new StoredMedia(path, file.length());
    }
}
