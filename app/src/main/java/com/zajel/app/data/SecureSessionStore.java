package com.zajel.app.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.zajel.app.domain.UserSession;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public final class SecureSessionStore {
    private static final String ALIAS = "zajel_session_key";
    private final SharedPreferences preferences;

    public SecureSessionStore(Context context) {
        preferences = context.getSharedPreferences(
                "zajel_session", Context.MODE_PRIVATE);
    }

    public void save(UserSession session) {
        try {
            preferences.edit()
                    .putString("token", seal(session.accessToken))
                    .putString("id", session.userId)
                    .putString("email", session.email)
                    .apply();
        } catch (Exception error) {
            throw new IllegalStateException(
                    "Unable to encrypt and save the session token", error);
        }
    }

    public UserSession read() {
        try {
            String token = preferences.getString("token", "");
            if (token.isEmpty()) {
                return null;
            }

            return new UserSession(
                    open(token),
                    preferences.getString("id", ""),
                    preferences.getString("email", ""));
        } catch (Exception error) {
            clear();
            return null;
        }
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    private String seal(String value) throws Exception {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.ENCRYPT_MODE,
                key(),
                new GCMParameterSpec(128, iv));

        byte[] encrypted = cipher.doFinal(
                value.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length,
                encrypted.length);

        return Base64.encodeToString(combined, Base64.NO_WRAP);
    }

    private String open(String value) throws Exception {
        byte[] combined = Base64.decode(value, Base64.NO_WRAP);

        if (combined.length < 13) {
            throw new IllegalArgumentException(
                    "Invalid encrypted session token");
        }

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.DECRYPT_MODE,
                key(),
                new GCMParameterSpec(128, combined, 0, 12));

        return new String(
                cipher.doFinal(combined, 12, combined.length - 12),
                StandardCharsets.UTF_8);
    }

    private SecretKey key() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        if (!keyStore.containsAlias(ALIAS)) {
            KeyGenerator generator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");

            generator.init(new KeyGenParameterSpec.Builder(
                    ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT
                            | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());

            generator.generateKey();
        }

        return ((KeyStore.SecretKeyEntry)
                keyStore.getEntry(ALIAS, null)).getSecretKey();
    }
              }
