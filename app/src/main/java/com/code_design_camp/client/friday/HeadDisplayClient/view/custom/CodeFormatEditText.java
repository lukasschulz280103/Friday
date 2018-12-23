package com.code_design_camp.client.friday.HeadDisplayClient.view.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

import androidx.appcompat.widget.AppCompatEditText;

public class CodeFormatEditText extends AppCompatEditText {
    private static final String LOGTAG = "CodeFormatEdittext";
    Resources res = getResources();
    private boolean isPrettyPrinting = false;
    private Runnable prettyPrinterRunnable = () -> {
        SpannableStringBuilder stb = new SpannableStringBuilder(this.getEditableText().toString());
        boolean isString = false;
        isPrettyPrinting = true;
        int stringStartPos = 0;
        int lastPointerPos = this.getSelectionStart();
        for (int i = 0; i < stb.length(); i++) {
            char c = stb.charAt(i);
            if (c == ',' && !isString) {
                stb.setSpan(new ForegroundColorSpan(res.getColor(R.color.highlight_orange)), i, i + 1, 0);
            } else if (c == '\"' || c == '\'') {
                if (isString) {
                    stb.setSpan(new ForegroundColorSpan(Color.GREEN), stringStartPos, i + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    stringStartPos = 0;
                    isString = false;
                } else {
                    isString = true;
                    stringStartPos = i;
                }
            }
        }
        this.setText(stb);
        this.setSelection(lastPointerPos);
        isPrettyPrinting = false;
    };
    private Thread prettyPrinter = new Thread(prettyPrinterRunnable);

    private OnEditorActionListener mEditorActionListener = (textView, i, keyEvent) -> {
        Log.d(LOGTAG, "OnEditorAction");
        SpannableStringBuilder ssb = new SpannableStringBuilder(textView.getText().toString());
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            ssb.insert(i, "}");
            textView.setText(ssb);
            return true;
        }
        return false;
    };

    public CodeFormatEditText(Context context) {
        super(context);
    }

    public CodeFormatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CodeFormatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onPreDraw() {
        this.setOnEditorActionListener(mEditorActionListener);
        return super.onPreDraw();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(LOGTAG, "editoraction");
        int pointerPos = this.getSelectionStart();
        SpannableStringBuilder editable = new SpannableStringBuilder(this.getText().toString());
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.d(LOGTAG, "enter");
            if (editable.charAt(pointerPos) == '{') {
                editable.insert(pointerPos + 1, "}");
            }
            this.setText(editable);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (!isPrettyPrinting && prettyPrinter != null) {
            prettyPrinter.run();
        }
    }
}
