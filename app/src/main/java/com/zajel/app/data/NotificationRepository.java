package com.zajel.app.data;

import com.zajel.app.domain.AppNotification;
import com.zajel.app.domain.NotificationPreferences;
import java.util.List;

public interface NotificationRepository {
    void list(Callback<List<AppNotification>> callback);
    void markRead(String notificationId, Callback<Void> callback);
    void preferences(Callback<NotificationPreferences> callback);
    void updatePreferences(NotificationPreferences preferences, Callback<Void> callback);
    interface Callback<T> { void success(T value); void error(Exception error); }
}
