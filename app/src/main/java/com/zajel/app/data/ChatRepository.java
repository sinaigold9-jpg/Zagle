package com.zajel.app.data;

import android.net.Uri;
import com.zajel.app.domain.*;import java.util.List;
public interface ChatRepository { void conversations(Callback<List<Conversation>> cb); void messages(String conversationId,Callback<List<Message>> cb); void sendText(String conversationId,String body,Callback<Message> cb); void sendAttachment(String conversationId,Uri source,String displayName,String mimeType,Callback<Message> cb); void updateStatus(String messageId,MessageStatus status,Callback<Boolean> cb); interface Callback<T>{void success(T value);void error(Exception error);} }
