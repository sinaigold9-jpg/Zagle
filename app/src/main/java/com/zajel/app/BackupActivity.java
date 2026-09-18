package com.zajel.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import com.zajel.app.data.LocalBackupRepository;

public final class BackupActivity extends Activity {
    private static final int CREATE = 10, OPEN = 11;
    private LocalBackupRepository backups; private CheckBox messages, media, files; private TextView status;
    @Override public void onCreate(Bundle state) { super.onCreate(state); backups = new LocalBackupRepository(this); render(); }
    private void render() { LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32,48,32,32); TextView title = new TextView(this); title.setText("النسخ الاحتياطي والمزامنة"); title.setTextSize(26); root.addView(title); TextView info = new TextView(this); info.setText("اختر Google Drive أو أي مزود تخزين من نافذة Android. لا يتم رفع البيانات تلقائياً دون اختيارك."); root.addView(info); messages = check("الرسائل والبيانات المنظمة", true); media = check("الوسائط", false); files = check("الملفات المستلمة", true); root.addView(messages); root.addView(media); root.addView(files); Button export = new Button(this); export.setText("إنشاء نسخة احتياطية"); root.addView(export); Button restore = new Button(this); restore.setText("فحص نسخة احتياطية"); root.addView(restore); status = new TextView(this); root.addView(status); export.setOnClickListener(v -> chooseDestination()); restore.setOnClickListener(v -> chooseSource()); setContentView(root); }
    private CheckBox check(String label, boolean checked) { CheckBox b = new CheckBox(this); b.setText(label); b.setChecked(checked); return b; }
    private void chooseDestination() { Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT); i.setType("application/json"); i.putExtra(Intent.EXTRA_TITLE, "zajel-backup.json"); startActivityForResult(i, CREATE); }
    private void chooseSource() { Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("application/json"); i.addCategory(Intent.CATEGORY_OPENABLE); startActivityForResult(i, OPEN); }
    @Override protected void onActivityResult(int request, int result, Intent data) { super.onActivityResult(request, result, data); if (result != RESULT_OK || data == null) return; Uri uri = data.getData(); new Thread(() -> { try { if (request == CREATE) { backups.exportTo(uri, messages.isChecked(), media.isChecked(), files.isChecked()); show("تم إنشاء النسخة في الموقع الذي اخترته"); } else show("النسخة صالحة: " + backups.inspect(uri).optString("format")); } catch (Exception e) { show("تعذر تنفيذ العملية"); } }).start(); }
    private void show(String value) { runOnUiThread(() -> status.setText(value)); }
}
