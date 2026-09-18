package com.zajel.app;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import com.zajel.app.core.AppContainer;
import com.zajel.app.domain.AppNotification;
import java.util.ArrayList;
import java.util.List;

public final class NotificationsActivity extends Activity {
    private AppContainer container; private LinearLayout root; private TextView status;
    @Override public void onCreate(Bundle state) { super.onCreate(state); container = ((ZajelApplication)getApplication()).container(); createChannel(); render(); load(); }
    private void render() { ScrollView scroll = new ScrollView(this); root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32,48,32,32); root.setBackgroundColor(Color.WHITE); scroll.addView(root); setContentView(scroll); TextView title = text("الإشعارات", 28); root.addView(title); Button settings = new Button(this); settings.setText("إعدادات الإشعارات"); root.addView(settings); settings.setOnClickListener(v -> startActivity(new Intent(this, NotificationSettingsActivity.class))); status = text("جارٍ تحميل الإشعارات…", 15); root.addView(status); }
    private void load() { container.notifications.list(new com.zajel.app.data.NotificationRepository.Callback<List<AppNotification>>() { public void success(List<AppNotification> rows) { runOnUiThread(() -> { status.setText(rows.isEmpty() ? "لا توجد إشعارات جديدة" : "الإشعارات"); for (AppNotification n : rows) { TextView item = text(n.title + "\n" + n.body, 16); root.addView(item); } }); } public void error(Exception e) { runOnUiThread(() -> status.setText("تعذر تحميل الإشعارات")); } }); }
    private void createChannel() { if (Build.VERSION.SDK_INT >= 26) { NotificationManager manager = getSystemService(NotificationManager.class); manager.createNotificationChannel(new NotificationChannel("zajel_general", "Zajel notifications", NotificationManager.IMPORTANCE_DEFAULT)); } }
    private TextView text(String value, int size) { TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(Color.rgb(24,45,61)); t.setGravity(Gravity.RIGHT); t.setPadding(0,12,0,12); return t; }
}
