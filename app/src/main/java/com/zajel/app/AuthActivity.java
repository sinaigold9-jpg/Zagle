package com.zajel.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.zajel.app.core.AppContainer;
import com.zajel.app.data.AuthRepository;
import com.zajel.app.domain.UserSession;

/** Premium native authentication screen for account creation and sign-in. */
public final class AuthActivity extends Activity {
    private AppContainer container;
    private LinearLayout form;
    private EditText firstName, lastName, identifier, password, confirmation;
    private TextView status;
    private boolean registration;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        container = ((ZajelApplication) getApplication()).container();
        if (container.auth.current() != null) openHome(); else render(false);
    }

    private void render(boolean signup) {
        registration = signup;
        ScrollView scroll = new ScrollView(this);
        form = new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL); form.setGravity(Gravity.CENTER_HORIZONTAL); form.setPadding(48, 64, 48, 36); form.setBackgroundColor(Color.rgb(247, 249, 252)); scroll.addView(form); setContentView(scroll);
        TextView brand = text("ZAJEL", 30, Color.rgb(24,45,61)); brand.setGravity(Gravity.CENTER); form.addView(brand);
        TextView subtitle = text(signup ? "أنشئ حسابك الآمن" : "مرحباً بعودتك", 20, Color.rgb(55,70,85)); subtitle.setGravity(Gravity.CENTER); form.addView(subtitle);
        if (signup) { firstName = field("الاسم الأول"); lastName = field("اسم العائلة"); form.addView(firstName); form.addView(lastName); }
        identifier = field("البريد الإلكتروني أو رقم الهاتف"); identifier.setInputType(InputType.TYPE_CLASS_TEXT); form.addView(identifier);
        password = field("كلمة المرور"); password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); form.addView(password);
        if (signup) { confirmation = field("تأكيد كلمة المرور"); confirmation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); form.addView(confirmation); }
        Button submit = button(signup ? "إنشاء الحساب" : "تسجيل الدخول"); form.addView(submit); submit.setOnClickListener(v -> submit());
        Button toggle = button(signup ? "لديك حساب؟ تسجيل الدخول" : "مستخدم جديد؟ إنشاء حساب"); form.addView(toggle); toggle.setOnClickListener(v -> render(!registration));
        status = text("", 14, Color.rgb(180,55,55)); form.addView(status);
    }

    private void submit() {
        String value = identifier.getText().toString().trim(), pass = password.getText().toString();
        if (value.isEmpty() || pass.length() < 6) { status.setText("أدخل وسيلة التسجيل وكلمة مر��ر من 6 أحرف على الأقل"); return; }
        if (registration) {
            String first = firstName.getText().toString().trim(), last = lastName.getText().toString().trim();
            if (first.isEmpty() || last.isEmpty()) { status.setText("اكتب الاسم الأول واسم العائلة"); return; }
            if (!pass.equals(confirmation.getText().toString())) { status.setText("كلمتا المرور غير متطابقتين"); return; }
            status.setText("جارٍ إنشاء الحساب…");
            container.auth.signUpIdentifier(first, last, value, pass, resultCallback(true));
        } else { status.setText("جارٍ تسجيل الدخول…"); container.auth.signInIdentifier(value, pass, resultCallback(false)); }
    }

    private AuthRepository.Callback<UserSession> resultCallback(boolean signup) {
        return new AuthRepository.Callback<UserSession>() {
            public void success(UserSession session) { runOnUiThread(() -> { if (session == null) status.setText("تم إنشاء الحساب. تحقق من رسالة التأكيد ثم سجل الدخول."); else openHome(); }); }
            public void error(Exception error) { runOnUiThread(() -> status.setText("تعذر إتمام العملية: " + (error.getMessage() == null ? "تحقق من البيانات" : error.getMessage()))); }
        };
    }

    private void openHome() { startActivity(new Intent(this, MainActivity.class)); finish(); }
    private EditText field(String hint) { EditText e = new EditText(this); e.setHint(hint); e.setTextSize(16); e.setSingleLine(true); e.setPadding(24, 8, 24, 8); e.setLayoutParams(new LinearLayout.LayoutParams(-1, 64)); return e; }
    private Button button(String label) { Button b = new Button(this); b.setText(label); b.setTextSize(15); b.setAllCaps(false); b.setLayoutParams(new LinearLayout.LayoutParams(-1, 62)); return b; }
    private TextView text(String value, int size, int color) { TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color); t.setPadding(0, 12, 0, 12); return t; }
}
