package com.code_design_camp.client.friday.HeadDisplayClient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

public class Feedback extends AppCompatActivity {
    AppCompatEditText feedback_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Toolbar t = findViewById(R.id.toolbar2);
        t.setTitle("Feedback");
        setSupportActionBar(t);

        feedback_email = findViewById(R.id.feedback_mail);
        feedback_email.setText(user.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_feedback,menu);
        return true;
    }
}
