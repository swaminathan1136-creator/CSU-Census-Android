package com.csu.census.ui;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageButton;

import com.csu.census.R;

public final class PasswordToggle {
    private PasswordToggle() {}

    public static void bind(EditText field, ImageButton button) {
        button.setOnClickListener(v -> {
            boolean showing = field.getTransformationMethod() instanceof HideReturnsTransformationMethod;
            if (showing) {
                field.setTransformationMethod(PasswordTransformationMethod.getInstance());
                button.setImageResource(R.drawable.ic_eye_off);
            } else {
                field.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                button.setImageResource(R.drawable.ic_eye);
            }
            field.setSelection(field.getText().length());
        });
    }
}