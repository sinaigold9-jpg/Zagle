package com.zajel.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.view.View;
import android.widget.*;
import androidx.core.content.FileProvider;
import com.zajel.app.data.LocalFileRepository;
import java.io.File;

public final class FileManagerActivity extends Activity {
    private static final int PICK = 20; private LocalFileRepository files; private LinearLayout list; private TextView status;
    @Override public void onCreate(Bundle state) { super.onCreate(state); files = new LocalFileRepository(this); render(); refresh(); }
    private void render() { LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(28,42,28,28); TextView title = new TextView(this); title.setText("Zajel Files"); root.addView(title); list = new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); root.addView(list); Button pick = new Button(this); pick.setText("Import file"); pick.setOnClickListener(v -> pickFile()); root.addView(pick); status = new TextView(this); root.setContentView(root); }
    private void pickFile() { Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("*/*"); i.addCategory(Intent.CATEGORY_OPENABLE); startActivityForResult(i, PICK); }
    @Override protected void onActivityResult(int request, int result, Intent data) { super.onActivityResult(request, result, data); if (request != PICK || result != RESULT_OK || data == null || data.getData() == null) return; Uri uri = data.getData(); try { File saved = files.importFile(uri, name(uri), getContentResolver()); show("Imported: " + saved.getName()); refresh(); } catch (Exception e) { show("Import failed: " + e.getMessage()); } }
    private String name(Uri uri) { Cursor c = getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null); if (c != null) try { if (c.moveToFirst()) return c.getString(c.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)); } finally { c.close(); } return "received_file"; }
    private void refresh() { runOnUiThread(() -> { if (list == null) return; list.removeAllViews(); for (File file : files.list()) { LinearLayout row = new LinearLayout(this); TextView label = new TextView(this); label.setText(file.getName()); row.addView(label); Button share = new Button(this); share.setText("Share"); share.setOnClickListener(v -> share(file)); row.addView(share); list.addView(row); } }); }
    private void share(File file) { Intent send = new Intent(Intent.ACTION_SEND); send.setType("application/octet-stream"); Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file); send.putExtra(Intent.EXTRA_STREAM, uri); send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); startActivity(Intent.createChooser(send, "Share file")); }
    private void show(String text) { runOnUiThread(() -> status.setText(text)); }
}
