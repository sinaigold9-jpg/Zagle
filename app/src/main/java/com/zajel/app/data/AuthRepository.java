package com.zajel.app.data;

import com.zajel.app.domain.UserSession;

public interface AuthRepository {
    UserSession current();
    void signIn(String email, String password, Callback<UserSession> callback);
    void signUp(String email, String password, Callback<UserSession> callback);
    void signInIdentifier(String identifier, String password, Callback<UserSession> callback);
    void signUpIdentifier(String firstName, String lastName, String identifier, String password, Callback<UserSession> callback);
    void signOut();
    interface Callback<T> { void success(T value); void error(Exception error); }
}
