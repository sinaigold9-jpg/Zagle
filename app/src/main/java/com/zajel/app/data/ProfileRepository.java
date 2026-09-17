package com.zajel.app.data;
import com.zajel.app.domain.Profile;
public interface ProfileRepository { void get(Callback<Profile> cb); void update(String username,String displayName,String avatarUrl,Callback<Profile> cb); interface Callback<T>{void success(T v);void error(Exception e);} }
