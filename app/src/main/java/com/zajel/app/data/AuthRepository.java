package com.zajel.app.data;

import com.zajel.app.domain.UserSession;
public interface AuthRepository { UserSession current(); void signIn(String email,String password,Callback<UserSession> cb); void signUp(String email,String password,Callback<UserSession> cb); void signOut(); interface Callback<T>{void success(T value);void error(Exception error);} }
