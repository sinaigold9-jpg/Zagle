package com.zajel.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/** TextView defaults that preserve readable locale direction and scalable text. */
public final class AccessibleTextView extends TextView {
    public AccessibleTextView(Context context) { super(context); configure(); }
    public AccessibleTextView(Context context, AttributeSet attrs) { super(context, attrs); configure(); }
    public AccessibleTextView(Context context, AttributeSet attrs, int style) { super(context, attrs, style); configure(); }
    private void configure() { setTextDirection(TEXT_DIRECTION_LOCALE); setLayoutDirection(LAYOUT_DIRECTION_LOCALE); setIncludeFontPadding(true); setMaxLines(8); }
}
