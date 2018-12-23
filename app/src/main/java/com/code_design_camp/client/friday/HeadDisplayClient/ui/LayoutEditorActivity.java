package com.code_design_camp.client.friday.HeadDisplayClient.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Theme;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

public class LayoutEditorActivity extends FridayActivity {
    private static final String LOGTAG = "LayoutEditor";
    AppCompatEditText edit_area;
    ArrayList<CharSequence> changeBackstack = new ArrayList<>();
    private Menu mMenu;
    private int tabmultiplier;
    private Resources res;
    private File configfile;
    TextWatcher editorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            changeBackstack.add(charSequence);
            setMenuState();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vr);
        setSupportActionBar(findViewById(R.id.editor_toolbar));

        res = getResources();
        edit_area = findViewById(R.id.contentedittext);
        edit_area.addTextChangedListener(editorWatcher);
        try {
            edit_area.setText(initFile().toString());
        } catch (IOException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        }
    }

    private JSONObject initFile() throws IOException {
        try {
            configfile = new File(getFilesDir(), "vrconfig.json");
            BufferedReader r = new BufferedReader(new FileReader(configfile));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = r.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }
            r.close();
            Log.d(LOGTAG, "result is " + result);
            return new JSONObject(result.toString());
        } catch (FileNotFoundException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        } catch (JSONException e) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configfile));
            writer.write("{}");
            writer.close();
            return initFile();
        }
        return null;
    }
    private String repeatString(int multiplier) {
        StringBuilder finalString = new StringBuilder();
        for (int i = 0; i <= multiplier; i++) {
            finalString.append(' ');
        }
        return finalString.toString();
    }

    private void setMenuState() {
        if (mMenu == null) {
            return;
        }
        if (changeBackstack.get(0).toString().equals(edit_area.getText().toString())) {
            mMenu.findItem(R.id.save).setEnabled(false);
        } else {
            mMenu.findItem(R.id.save).setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_toolbar, menu);
        mMenu = menu;
        setMenuState();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save: {
                try {
                    //Using constructor to catch possible invalid json
                    new JSONObject(edit_area.getText().toString());
                    FileWriter writer = new FileWriter(configfile);
                    String[] content = edit_area.getText().toString().split("\\n");
                    for (String line : content) {
                        writer.write(line);
                    }
                    writer.close();
                    Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this);
                    errorBuilder.setTitle("Invalid JSON");
                    errorBuilder.setMessage(e.getMessage());
                    errorBuilder.setPositiveButton(android.R.string.ok, null);
                    errorBuilder.create().show();
                } catch (IOException e) {
                    Log.e(LOGTAG, e.getLocalizedMessage(), e);
                }
            }
        }
        return true;
    }
}
