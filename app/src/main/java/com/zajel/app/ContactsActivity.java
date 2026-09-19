package com.zajel.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;
import com.zajel.app.core.AppContainer;
import com.zajel.app.core.InviteConfig;
import com.zajel.app.domain.ContactEntry;
import java.util.List;

public final class ContactsActivity extends Activity {
    private static final int CONTACTS_PERMISSION = 42; private AppContainer container; private LinearLayout list; private TextView status;
    @Override public void onCreate(Bundle state) { super.onCreate(state); container = ((ZajelApplication)getApplication()).container(); render(); if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) load(); else requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION); }
    private void render() { LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(28,42,28,28); TextView title = new TextView(this); title.setText("جهات الاتصال"); title.setTextSize(28); root.addView(title); status = new TextView(this); root.addView(status); list = new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); root.addView(list); setContentView(root); }
    @Override public void onRequestPermissionsResult(int request, String[] permissions, int[] results) { super.onRequestPermissionsResult(request, permissions, results); if (request == CONTACTS_PERMISSION && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) load(); else status.setText("يلزم السماح بقراءة جهات الاتصال لإظهارها"); }
    private void load() { status.setText("جارٍ تحميل جهات الاتصال…"); container.contacts.readDeviceContacts(new com.zajel.app.data.ContactsRepository.Callback<List<ContactEntry>>() { public void success(List<ContactEntry> rows) { runOnUiThread(() -> { status.setText(rows.isEmpty() ? "لا توجد جهات اتصال بأرقام هاتف" : "جهات الاتصال"); for (ContactEntry c : rows) { LinearLayout row = new LinearLayout(ContactsActivity.this); row.setOrientation(LinearLayout.HORIZONTAL); TextView name = new TextView(ContactsActivity.this); name.setText(c.name + "\n" + c.identifier()); name.setTextSize(16); row.addView(name, new LinearLayout.LayoutParams(0, -2, 1)); Button invite = new Button(ContactsActivity.this); invite.setText("دعوة"); invite.setOnClickListener(v -> invite(c)); row.addView(invite); list.addView(row); } }); } public void error(Exception e) { runOnUiThread(() -> status.setText("تعذر قراءة جهات الاتصال")); } }); }
    private void invite(ContactEntry contact) { Intent share = new Intent(Intent.ACTION_SEND); share.setType("text/plain"); share.putExtra(Intent.EXTRA_TEXT, "انضم إلى Zajel للتواصل بأمان: " + InviteConfig.ZAJEL_INVITE_URL); startActivity(Intent.createChooser(share, "إرسال دعوة")); }
}
