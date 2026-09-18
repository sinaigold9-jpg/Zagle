package com.zajel.app.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import com.zajel.app.domain.ContactEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AndroidContactsRepository implements ContactsRepository {
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public AndroidContactsRepository(Context context) { this.context = context.getApplicationContext(); }
    @Override public void readDeviceContacts(Callback<List<ContactEntry>> callback) {
        executor.execute(() -> {
            try {
                LinkedHashMap<String, ContactEntry> unique = new LinkedHashMap<>();
                Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER},
                        null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
                if (cursor != null) {
                    try { while (cursor.moveToNext()) { String name = cursor.getString(0); String phone = cursor.getString(1); if (phone != null && !phone.trim().isEmpty()) unique.putIfAbsent(phone, new ContactEntry(name == null ? "" : name, phone, "")); } }
                    finally { cursor.close(); }
                }
                callback.success(new ArrayList<>(unique.values()));
            } catch (Exception error) { callback.error(error); }
        });
    }
}
