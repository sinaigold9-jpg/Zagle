package com.zajel.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import com.zajel.app.core.AppContainer;
import com.zajel.app.data.CommunityRepository;
import com.zajel.app.domain.Community;
import java.util.List;

/** Native Community list; group/channel relations remain repository-backed. */
public final class CommunitiesActivity extends Activity {
    private LinearLayout root; private AppContainer container; private TextView status;
    @Override public void onCreate(Bundle state) { super.onCreate(state); container = ((ZajelApplication) getApplication()).container(); render(); load(); }
    private void render() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32, 48, 32, 24); root.setBackgroundColor(Color.WHITE); setContentView(root);
        TextView title = new TextView(this); title.setText("Communities"); title.setTextSize(28); root.addView(title);
        Button create = new Button(this); create.setText("Create community"); root.addView(create); create.setOnClickListener(v -> createCommunity());
        status = new TextView(this); root.addView(status);
    }
    private void load() {
        status.setText("Loading communities…");
        container.communities.myCommunities(new CommunityRepository.Callback<List<Community>>() {
            @Override public void success(List<Community> values) { runOnUiThread(() -> { status.setText(values.isEmpty() ? "No communities" : "Your communities"); for (Community value : values) addCommunity(value); }); }
            @Override public void error(Exception error) { runOnUiThread(() -> status.setText("Unable to load communities")); }
        });
    }
    private void addCommunity(Community community) { TextView item = new TextView(this); item.setText(community.title + "\n" + community.description); item.setTextSize(18); item.setPadding(0, 16, 0, 16); root.addView(item); }
    private void createCommunity() {
        EditText input = new EditText(this); input.setHint("Community name");
        new AlertDialog.Builder(this).setTitle("Create community").setView(input).setPositiveButton("Create", (dialog, which) ->
                container.communities.create(input.getText().toString().trim(), "", new CommunityRepository.Callback<Community>() {
                    @Override public void success(Community value) { runOnUiThread(() -> load()); }
                    @Override public void error(Exception error) { runOnUiThread(() -> status.setText("Community creation failed")); }
                })).setNegativeButton("Cancel", null).show();
    }
}
