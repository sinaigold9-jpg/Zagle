package com.zajel.app.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Shared native visual primitives for consistent RTL-friendly screens. */
public final class NativeUi {
    public static final int INK = Color.rgb(24, 45, 61);
    public static final int MUTED = Color.rgb(91, 108, 121);
    public static final int SURFACE = Color.rgb(247, 249, 252);
    public static final int ACCENT = Color.rgb(39, 108, 122);
    private NativeUi() { }

    public static void configureRoot(View root) {
        root.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        root.setContentDescription(root.getContentDescription());
    }
    public static TextView heading(Context context, CharSequence text) {
        TextView view = new TextView(context);
        view.setText(text); view.setTextSize(26); view.setTextColor(INK); view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setGravity(Gravity.START); view.setPadding(0, 12, 0, 16); view.setContentDescription(text);
        return view;
    }
    public static TextView body(Context context, CharSequence text) {
        TextView view = new TextView(context);
        view.setText(text); view.setTextSize(16); view.setTextColor(MUTED); view.setGravity(Gravity.START); view.setPadding(0, 8, 0, 8); view.setContentDescription(text);
        return view;
    }
    public static Button action(Context context, CharSequence text) {
        Button button = new Button(context); button.setText(text); button.setTextSize(15); button.setAllCaps(false); button.setMinHeight(52); button.setContentDescription(text); return button;
    }
    public static LinearLayout page(Context context) {
        LinearLayout page = new LinearLayout(context); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(24, 32, 24, 24); page.setBackgroundColor(SURFACE); configureRoot(page); return page;
    }
}
