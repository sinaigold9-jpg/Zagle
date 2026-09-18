package com.zajel.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.zajel.app.core.AppContainer;
import com.zajel.app.domain.NotificationPreferences;

public final class NotificationSettingsActivity extends Activity {
    private AppContainer container; private Switch enabled, messages, groups, rooms, announcements; private TextView status;
    @Override public void onCreate(Bundle state) { super.onCreate(state); container = ((ZajelApplication)getApplication()).container(); render(); load(); }
    private void render() { LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32,48,32,32); enabled = toggle("تفعيل الإشعارات"); messages = toggle("الرسائل"); groups = toggle("المجموعات"); rooms = toggle("الغرف"); announcements = toggle("الإعلانات والتنبيهات العامة"); root.addView(enabled); root.addView(messages); root.addView(groups); root.addView(rooms); root.addView(announcements); Button save = new Button(this); save.setText("حفظ"); root.addView(save); status = new TextView(this); root.addView(status); save.setOnClickListener(v -> save()); setContentView(root); }
    private void load() { container.notifications.preferences(new com.zajel.app.data.NotificationRepository.Callback<NotificationPreferences>() { public void success(NotificationPreferences p) { runOnUiThread(() -> { enabled.setChecked(p.enabled); messages.setChecked(p.messages); groups.setChecked(p.groups); rooms.setChecked(p.rooms); announcements.setChecked(p.announcements); }); } public void error(Exception e) { runOnUiThread(() -> status.setText("تعذر تحميل الإعدادات")); } }); }
    private void save() { container.notifications.updatePreferences(new NotificationPreferences(enabled.isChecked(), messages.isChecked(), groups.isChecked(), rooms.isChecked(), announcements.isChecked()), new com.zajel.app.data.NotificationRepository.Callback<Void>() { public void success(Void v) { runOnUiThread(() -> status.setText("تم حفظ الإعدادات")); } public void error(Exception e) { runOnUiThread(() -> status.setText("تعذر حفظ الإعدادات")); } }); }
    private Switch toggle(String label) { Switch s = new Switch(this); s.setText(label); s.setTextSize(17); s.setPadding(0,16,0,16); return s; }
}
