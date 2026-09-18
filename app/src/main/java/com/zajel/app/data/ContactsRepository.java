package com.zajel.app.data;

import com.zajel.app.domain.AppNotification;
import com.zajel.app.domain.NotificationPreferences;
import java.util.List;

public interface ContactsRepository {
    void readDeviceContacts(Callback<List<com.zajel.app.domain.ContactEntry>> callback);
    interface Callback<T> { void success(T value); void error(Exception error); }
}
