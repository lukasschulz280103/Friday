package com.code_design_camp.client.friday.HeadDisplayClient.Util;

import android.content.Context;
import android.widget.EditText;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

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
