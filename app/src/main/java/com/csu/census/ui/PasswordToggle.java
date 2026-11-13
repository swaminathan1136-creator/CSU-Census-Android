package com.csu.census.ui;

import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;

import com.csu.census.R;

public final class PasswordToggle {

    private PasswordToggle() {
    }

    public static void bind(EditText field, ImageButton button) {
        if (field == null || button == null) {
            return;
        }
        button.setOnClickListener(view -> {
            boolean hidden = (field.getInputType()
                    & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD;
            if (hidden) {
                field.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                button.setImageResource(R.drawable.ic_eye);
            } else {
                field.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                button.setImageResource(R.drawable.ic_eye_off);
            }
            field.setSelection(field.getText().length());
        });
    }
}