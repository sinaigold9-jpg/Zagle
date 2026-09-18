package com.zajel.app.data;

import com.zajel.app.domain.PrivacySettings;
import com.zajel.app.domain.SessionDevice;
import java.util.List;

public interface SecurityRepository {
    void privacySettings(Callback<PrivacySettings> callback);
    void updatePrivacySettings(PrivacySettings settings, Callback<Void> callback);
    void sessions(Callback<List<SessionDevice>> callback);
    void revokeSession(String sessionId, Callback<Void> callback);
    void currentSession(Callback<SessionDevice> callback);

    interface Callback<T> {
        void success(T value);
        void error(Exception error);
    }
}
