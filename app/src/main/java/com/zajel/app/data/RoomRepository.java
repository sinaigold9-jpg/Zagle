package com.zajel.app.data;

import com.zajel.app.domain.Room;
import com.zajel.app.domain.RoomMember;
import com.zajel.app.domain.RoomMessage;
import java.util.List;

public interface RoomRepository {
    void myRooms(Callback<List<Room>> cb);
    void createRoom(String title, String description, boolean isPrivate, Callback<Room> cb);
    void joinRoom(String roomId, Callback<RoomMember> cb);
    void members(String roomId, Callback<List<RoomMember>> cb);
    void messages(String roomId, Callback<List<RoomMessage>> cb);
    void sendMessage(String roomId, String body, Callback<RoomMessage> cb);

    interface Callback<T> {
        void success(T value);
        void error(Exception error);
    }
}
