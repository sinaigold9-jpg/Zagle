package com.zajel.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.zajel.app.core.AppContainer;
import com.zajel.app.data.ChatRepository;
import com.zajel.app.data.GroupRepository;
import com.zajel.app.data.ProfileRepository;
import com.zajel.app.data.RoomRepository;
import com.zajel.app.domain.Conversation;
import com.zajel.app.domain.Group;
import com.zajel.app.domain.Message;
import com.zajel.app.domain.Profile;
import com.zajel.app.domain.Room;
import com.zajel.app.domain.RoomMessage;
import com.zajel.app.domain.UserSession;
import java.util.List;

public final class MainActivity extends Activity {
    private LinearLayout root;
    private EditText email;
    private EditText password;
    private AppContainer c;
    private TextView status;
    private Conversation activeChat;
    private Room activeRoom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = ((ZajelApplication) getApplication()).container();
        if (c.auth.current() != null) {
            showHome();
        } else {
            showAuth();
        }
    }

    private TextView title(String s) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextSize(26);
        v.setTextColor(Color.rgb(24, 45, 61));
        v.setPadding(0, 0, 0, 28);
        return v;
    }

    private void base() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 70, 40, 30);
        root.setBackgroundColor(Color.WHITE);
        setContentView(root);
    }

    private void showAuth() {
        base();
        root.addView(title("Zajel"));
        email = new EditText(this);
        email.setHint("Email");
        password = new EditText(this);
        password.setHint("Password");
        password.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        root.addView(email);
        root.addView(password);
        Button signIn = new Button(this);
        signIn.setText("Sign in");
        Button signUp = new Button(this);
        signUp.setText("Create account");
        root.addView(signIn);
        root.addView(signUp);
        status = new TextView(this);
        root.addView(status);
        signIn.setOnClickListener(v -> auth(false));
        signUp.setOnClickListener(v -> auth(true));
    }

    private void auth(boolean signUpMode) {
        status.setText("Connecting…");
        c.auth.signInOrUp(email.getText().toString().trim(), password.getText().toString(), signUpMode,
                new com.zajel.app.data.AuthRepository.Callback<UserSession>() {
                    @Override public void success(UserSession session) { runOnUiThread(() -> showHome()); }
                    @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
                });
    }

    private void showHome() {
        base();
        root.addView(title("Home"));
        TextView helper = new TextView(this);
        helper.setText("Your conversations");
        root.addView(helper);
        ListView list = new ListView(this);
        root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        status = new TextView(this);
        root.addView(status);
        Button groups = new Button(this);
        groups.setText("Groups");
        Button rooms = new Button(this);
        rooms.setText("Rooms");
        Button profile = new Button(this);
        profile.setText("Profile");
        root.addView(groups);
        root.addView(rooms);
        root.addView(profile);
        groups.setOnClickListener(v -> showGroups());
        rooms.setOnClickListener(v -> showRooms());
        profile.setOnClickListener(v -> showProfile());
        c.chats.conversations(new ChatRepository.Callback<List<Conversation>>() {
            @Override public void success(List<Conversation> value) {
                runOnUiThread(() -> {
                    status.setText(value.isEmpty() ? "No conversations yet" : "");
                    String[] labels = new String[value.size()];
                    for (int i = 0; i < value.size(); i++) labels[i] = value.get(i).title;
                    list.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, labels));
                    list.setOnItemClickListener((parent, view, position, id) -> showChat(value.get(position)));
                });
            }
            @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
        });
    }

    private void showChat(Conversation conversation) {
        activeChat = conversation;
        base();
        root.addView(title(conversation.title));
        ListView messages = new ListView(this);
        root.addView(messages, new LinearLayout.LayoutParams(-1, 0, 1));
        EditText input = new EditText(this);
        input.setHint("Message");
        Button send = new Button(this);
        send.setText("Send");
        Button back = new Button(this);
        back.setText("Back");
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(input, new LinearLayout.LayoutParams(0, -2, 1f));
        row.addView(send);
        root.addView(row);
        root.addView(back);
        status = new TextView(this);
        root.addView(status);
        back.setOnClickListener(v -> showHome());
        send.setOnClickListener(v -> {
            String body = input.getText().toString().trim();
            if (body.isEmpty()) return;
            c.chats.sendText(conversation.id, body, new ChatRepository.Callback<Message>() {
                @Override public void success(Message message) { runOnUiThread(() -> { input.setText(""); loadChatMessages(messages, conversation.id); }); }
                @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
            });
        });
        loadChatMessages(messages, conversation.id);
    }

    private void loadChatMessages(ListView messages, String conversationId) {
        c.chats.messages(conversationId, new ChatRepository.Callback<List<Message>>() {
            @Override public void success(List<Message> value) {
                runOnUiThread(() -> {
                    String[] lines = new String[value.size()];
                    for (int i = 0; i < value.size(); i++) lines[i] = value.get(i).body;
                    messages.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, lines));
                });
            }
            @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
        });
    }

    private void showGroups() {
        base();
        root.addView(title("Groups"));
        ListView list = new ListView(this);
        root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        status = new TextView(this);
        root.addView(status);
        Button create = new Button(this);
        create.setText("Create group");
        Button back = new Button(this);
        back.setText("Back");
        root.addView(create);
        root.addView(back);
        create.setOnClickListener(v -> createGroupPrompt());
        back.setOnClickListener(v -> showHome());
        c.groups.myGroups(new GroupRepository.Callback<List<Group>>() {
            @Override public void success(List<Group> groups) {
                runOnUiThread(() -> {
                    status.setText(groups.isEmpty() ? "No groups yet" : "");
                    String[] labels = new String[groups.size()];
                    for (int i = 0; i < groups.size(); i++) labels[i] = groups.get(i).title;
                    list.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, labels));
                });
            }
            @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
        });
    }

    private void createGroupPrompt() {
        LinearLayout dialogRoot = new LinearLayout(this);
        dialogRoot.setOrientation(LinearLayout.VERTICAL);
        EditText title = new EditText(this);
        title.setHint("Group title");
        EditText desc = new EditText(this);
        desc.setHint("Description");
        dialogRoot.addView(title);
        dialogRoot.addView(desc);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("New group")
                .setView(dialogRoot)
                .setPositiveButton("Create", (di, which) -> {
                    String groupTitle = title.getText().toString().trim();
                    String groupDesc = desc.getText().toString().trim();
                    if (groupTitle.isEmpty()) {
                        status.setText("Group title required");
                        return;
                    }
                    c.groups.createGroup(groupTitle, groupDesc, "", false, new GroupRepository.Callback<Group>() {
                        @Override public void success(Group group) { runOnUiThread(() -> showGroups()); }
                        @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
                    });
                })
                .setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showRooms() {
        base();
        root.addView(title("Rooms"));
        ListView list = new ListView(this);
        root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        status = new TextView(this);
        root.addView(status);
        Button create = new Button(this);
        create.setText("Create room");
        Button back = new Button(this);
        back.setText("Back");
        root.addView(create);
        root.addView(back);
        create.setOnClickListener(v -> createRoomPrompt());
        back.setOnClickListener(v -> showHome());
        c.rooms.myRooms(new com.zajel.app.data.RoomRepository.Callback<List<Room>>() {
            @Override public void success(List<Room> rooms) {
                runOnUiThread(() -> {
                    status.setText(rooms.isEmpty() ? "No rooms yet" : "");
                    String[] labels = new String[rooms.size()];
                    for (int i = 0; i < rooms.size(); i++) labels[i] = rooms.get(i).title;
                    list.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, labels));
                    list.setOnItemClickListener((parent, view, position, id) -> showRoomChat(rooms.get(position)));
                });
            }
            @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
        });
    }

    private void createRoomPrompt() {
        LinearLayout dialogRoot = new LinearLayout(this);
        dialogRoot.setOrientation(LinearLayout.VERTICAL);
        EditText title = new EditText(this);
        title.setHint("Room title");
        EditText desc = new EditText(this);
        desc.setHint("Description");
        dialogRoot.addView(title);
        dialogRoot.addView(desc);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("New room")
                .setView(dialogRoot)
                .setPositiveButton("Create", (di, which) -> {
                    String roomTitle = title.getText().toString().trim();
                    String roomDesc = desc.getText().toString().trim();
                    if (roomTitle.isEmpty()) {
                        status.setText("Room title required");
                        return;
                    }
                    c.rooms.createRoom(roomTitle, roomDesc, false, new com.zajel.app.data.RoomRepository.Callback<Room>() {
                        @Override public void success(Room room) { runOnUiThread(() -> showRooms()); }
                        @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
                    });
                })
                .setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showRoomChat(Room room) {
        activeRoom = room;
        base();
        root.addView(title(room.title));
        ListView messages = new ListView(this);
        root.addView(messages, new LinearLayout.LayoutParams(-1, 0, 1));
        EditText input = new EditText(this);
        input.setHint("Room message");
        Button send = new Button(this);
        send.setText("Send");
        Button back = new Button(this);
        back.setText("Back");
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(input, new LinearLayout.LayoutParams(0, -2, 1f));
        row.addView(send);
        root.addView(row);
        root.addView(back);
        status = new TextView(this);
        root.addView(status);
        back.setOnClickListener(v -> showRooms());
        send.setOnClickListener(v -> {
            String body = input.getText().toString().trim();
            if (body.isEmpty()) return;
            c.rooms.sendMessage(room.id, body, new RoomRepository.Callback<RoomMessage>() {
                @Override public void success(RoomMessage message) { runOnUiThread(() -> { input.setText(""); loadRoomMessages(messages, room.id); }); }
                @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
            });
        });
        loadRoomMessages(messages, room.id);
    }

    private void loadRoomMessages(ListView messages, String roomId) {
        c.rooms.messages(roomId, new RoomRepository.Callback<List<RoomMessage>>() {
            @Override public void success(List<RoomMessage> value) {
                runOnUiThread(() -> {
                    String[] lines = new String[value.size()];
                    for (int i = 0; i < value.size(); i++) lines[i] = value.get(i).body;
                    messages.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, lines));
                });
            }
            @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
        });
    }

    private void showProfile() {
        base();
        root.addView(title("Your profile"));
        EditText user = new EditText(this);
        user.setHint("Username");
        EditText name = new EditText(this);
        name.setHint("Display name");
        EditText avatar = new EditText(this);
        avatar.setHint("Avatar URL");
        root.addView(user);
        root.addView(name);
        root.addView(avatar);
        Button save = new Button(this);
        save.setText("Save profile");
        Button back = new Button(this);
        back.setText("Back");
        Button signOut = new Button(this);
        signOut.setText("Sign out");
        root.addView(save);
        root.addView(back);
        root.addView(signOut);
        status = new TextView(this);
        root.addView(status);
        c.profiles.get(new ProfileRepository.Callback<Profile>() {
            @Override public void success(Profile p) { runOnUiThread(() -> { user.setText(p.username); name.setText(p.displayName); avatar.setText(p.avatarUrl); }); }
            @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
        });
        save.setOnClickListener(v -> c.profiles.update(user.getText().toString(), name.getText().toString(), avatar.getText().toString(), new ProfileRepository.Callback<Profile>() {
            @Override public void success(Profile profile) { runOnUiThread(() -> status.setText("Profile saved")); }
            @Override public void error(Exception e) { runOnUiThread(() -> status.setText(e.getMessage())); }
        }));
        back.setOnClickListener(v -> showHome());
        signOut.setOnClickListener(v -> { c.auth.signOut(); showAuth(); });
    }
}
