package com.zajel.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
import com.zajel.app.domain.ExploreItem;
import com.zajel.app.domain.PublicAd;
import com.zajel.app.domain.PublicSuggestion;
import java.util.ArrayList;
import java.util.List;

/** Native Explore screen for phases 9 and 10. It renders server data only; no mock content. */
public final class ExploreActivity extends Activity {
    private LinearLayout content;
    private EditText search;
    private TextView status;
    private com.zajel.app.core.AppContainer container;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        container = ((ZajelApplication) getApplication()).container();
        render();
        loadPublicContent();
    }

    private void render() {
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(32, 48, 32, 32);
        content.setBackgroundColor(Color.WHITE);
        scroll.addView(content);
        setContentView(scroll);
        TextView title = new TextView(this);
        title.setText("Explore"); title.setTextSize(28); title.setTextColor(Color.rgb(24,45,61));
        content.addView(title);
        search = new EditText(this); search.setHint("Search public users, groups, and rooms"); content.addView(search);
        Button find = new Button(this); find.setText("Search"); content.addView(find); find.setOnClickListener(v -> runSearch());
        status = new TextView(this); status.setPadding(0, 16, 0, 16); content.addView(status);
    }

    private void loadPublicContent() {
        container.explore.suggestions(new com.zajel.app.data.ExploreRepository.Callback<List<PublicSuggestion>>() {
            public void success(List<PublicSuggestion> rows) { runOnUiThread(() -> section("Suggestions", rowsToText(rows))); }
            public void error(Exception e) { showError(e); }
        });
        container.explore.publicAds(new com.zajel.app.data.ExploreRepository.Callback<List<PublicAd>>() {
            public void success(List<PublicAd> rows) { runOnUiThread(() -> section("Public notices", adsToText(rows))); }
            public void error(Exception e) { showError(e); }
        });
    }

    private void runSearch() {
        status.setText("Searching public content…");
        container.explore.search(search.getText().toString(), new com.zajel.app.data.ExploreRepository.Callback<List<ExploreItem>>() {
            public void success(List<ExploreItem> rows) { runOnUiThread(() -> { status.setText(rows.isEmpty() ? "No public results" : "Public results"); section("Results", itemsToText(rows)); }); }
            public void error(Exception e) { showError(e); }
        });
    }

    private void section(String heading, List<String> values) {
        TextView label = new TextView(this); label.setText(heading); label.setTextSize(20); label.setTextColor(Color.rgb(24,45,61)); label.setPadding(0, 20, 0, 8); content.addView(label);
        for (String value : values) { TextView row = new TextView(this); row.setText(value); row.setTextSize(16); row.setPadding(0, 8, 0, 8); content.addView(row); }
        if (values.isEmpty()) { TextView empty = new TextView(this); empty.setText("Nothing available yet"); content.addView(empty); }
    }

    private List<String> itemsToText(List<ExploreItem> rows) { List<String> out = new ArrayList<>(); for (ExploreItem x : rows) out.add(x.type + ": " + x.title + (x.description.isEmpty() ? "" : "\n" + x.description)); return out; }
    private List<String> rowsToText(List<PublicSuggestion> rows) { List<String> out = new ArrayList<>(); for (PublicSuggestion x : rows) out.add(x.title + (x.reason.isEmpty() ? "" : "\n" + x.reason)); return out; }
    private List<String> adsToText(List<PublicAd> rows) { List<String> out = new ArrayList<>(); for (PublicAd x : rows) out.add(x.title + "\n" + x.body); return out; }
    private void showError(Exception e) { runOnUiThread(() -> status.setText("Explore unavailable: " + e.getMessage())); }
}
