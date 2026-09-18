package com.zajel.app.data;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class LocalFileRepository {
    private final File directory;
    public LocalFileRepository(Context context) { directory = new File(context.getApplicationContext().getFilesDir(), "received"); if (!directory.exists()) directory.mkdirs(); }
    public File importFile(Uri source, String displayName, android.content.ContentResolver resolver) throws Exception {
        String safe = displayName == null || displayName.trim().isEmpty() ? "received_file" : displayName.replaceAll("[^a-zA-Z0-9._-]", "_");
        File target = new File(directory, System.currentTimeMillis() + "_" + safe);
        try (java.io.InputStream in = resolver.openInputStream(source); FileOutputStream out = new FileOutputStream(target)) { if (in == null) throw new IllegalStateException("Cannot open file"); byte[] buffer = new byte[8192]; int n; while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n); }
        return target;
    }
    public List<File> list() { File[] files = directory.listFiles(); List<File> result = new ArrayList<>(); if (files != null) for (File file : files) if (file.isFile()) result.add(file); return result; }
}
