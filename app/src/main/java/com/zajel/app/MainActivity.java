package com.zajel.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Home screen and navigation hub. Feature implementations live in their own activities. */
public final class MainActivity extends Activity {
    private LinearLayout root;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (((ZajelApplication) getApplication()).container().auth.current() == null) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }
        showHome();
    }

    private void showHome() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 70, 40, 30);
        root.setBackgroundColor(Color.WHITE);
        setContentView(root);

        TextView title = new TextView(this);
        title.setText("Zajel\nHome");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(24, 45, 61));
        title.setPadding(0, 0, 0, 24);
        root.addView(title);

        addNavigation("Explore", ExploreActivity.class);
        addNavigation("Contacts", ContactsActivity.class);
        addNavigation("Notifications", NotificationsActivity.class);
        addNavigation("Channels", ChannelsActivity.class);
        addNavigation("Communities", CommunitiesActivity.class);
        addNavigation("Privacy & Security", PrivacySecurityActivity.class);
        addNavigation("Backup", BackupActivity.class);
        addNavigation("File manager", FileManagerActivity.class);
    }

    private void addNavigation(String label, Class<? extends Activity> destination) {
        Button button = new Button(this);
        button.setText(label);
        button.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, destination)));
        root.addView(button, new LinearLayout.LayoutParams(-1, -2));
    }
}
