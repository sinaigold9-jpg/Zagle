package com.zajel.app.data;
import com.zajel.app.domain.*;import java.util.List;
public interface ChatRepository { void conversations(Callback<List<Conversation>> cb); void messages(String conversationId,Callback<List<Message>> cb); void sendText(String conversationId,String body,Callback<Message> cb); interface Callback<T>{void success(T value);void error(Exception error);} }
