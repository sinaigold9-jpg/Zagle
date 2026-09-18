package com.zajel.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import com.zajel.app.core.AppContainer;
import com.zajel.app.data.ChannelRepository;
import com.zajel.app.domain.Channel;
import com.zajel.app.domain.ChannelPost;
import java.util.List;

/** Native channel list and channel actions. External URL sharing is intentionally not used. */
public final class ChannelsActivity extends Activity {
    private LinearLayout root; private AppContainer container; private TextView status;
    @Override public void onCreate(Bundle state) { super.onCreate(state); container = ((ZajelApplication) getApplication()).container(); render(); load(); }
    private void render() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32, 48, 32, 24); root.setBackgroundColor(Color.WHITE); setContentView(root);
        TextView title = new TextView(this); title.setText("Channels"); title.setTextSize(28); root.addView(title);
        Button create = new Button(this); create.setText("Create channel"); root.addView(create); create.setOnClickListener(v -> createChannel());
        status = new TextView(this); root.addView(status);
    }
    private void load() {
        status.setText("Loading channels…");
        container.channels.publicChannels(new ChannelRepository.Callback<List<Channel>>() {
            @Override public void success(List<Channel> values) { runOnUiThread(() -> { status.setText(values.isEmpty() ? "No public channels" : "Public channels"); for (Channel value : values) addChannel(value); }); }
            @Override public void error(Exception error) { runOnUiThread(() -> status.setText("Unable to load channels")); }
        });
    }
    private void addChannel(Channel channel) { Button item = new Button(this); item.setText(channel.title); root.addView(item); item.setOnClickListener(v -> openChannel(channel)); }
    private void createChannel() {
        EditText input = new EditText(this); input.setHint("Channel name");
        new AlertDialog.Builder(this).setTitle("Create channel").setView(input).setPositiveButton("Create", (dialog, which) ->
                container.channels.create(input.getText().toString().trim(), "", false, new ChannelRepository.Callback<Channel>() {
                    @Override public void success(Channel value) { runOnUiThread(this::reload); }
                    private void reload() { load(); }
                    @Override public void error(Exception error) { runOnUiThread(() -> status.setText("Channel creation failed")); }
                })).setNegativeButton("Cancel", null).show();
    }
    private void openChannel(Channel channel) {
        EditText input = new EditText(this); input.setHint("Write a post");
        new AlertDialog.Builder(this).setTitle(channel.title).setView(input)
                .setPositiveButton("Publish", (d, w) -> container.channels.publish(channel.id, input.getText().toString(), emptyPostCallback()))
                .setNeutralButton("Follow", (d, w) -> container.channels.follow(channel.id, emptyVoidCallback())).setNegativeButton("Close", null).show();
    }
    private ChannelRepository.Callback<ChannelPost> emptyPostCallback() { return new ChannelRepository.Callback<ChannelPost>() { public void success(ChannelPost value) {} public void error(Exception error) {} }; }
    private ChannelRepository.Callback<Void> emptyVoidCallback() { return new ChannelRepository.Callback<Void>() { public void success(Void value) {} public void error(Exception error) {} }; }
}
