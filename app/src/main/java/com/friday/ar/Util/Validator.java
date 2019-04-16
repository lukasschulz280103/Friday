package com.friday.ar.Util;

import android.content.Context;
import android.widget.EditText;

import com.friday.ar.R;

public class Validator {
    public static boolean validateEmailInput(Context c, EditText email) {
        String text = email.getText().toString();
        boolean isValid = validateEmail(text);
        if (!isValid) {
            email.setError(c.getString(R.string.mail_invalid_error));
        }
        return isValid;
    }

    public static boolean validateEmail(String email) {
        return !"".equals(email) && (email.contains("@") && email.contains("."));
    }
}
