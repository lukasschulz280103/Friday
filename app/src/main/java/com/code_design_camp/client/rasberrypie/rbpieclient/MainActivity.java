package com.code_design_camp.client.rasberrypie.rbpieclient;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawer;
    NavigationView navelems;
    ViewSwitcher vswitcher;
    FirebaseDatabase fbdb;

    TextView statusview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar t = findViewById(R.id.toolbar_main);
        setSupportActionBar(t);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawer = findViewById(R.id.main_drawer);
        navelems = findViewById(R.id.navigation_view_main);
        statusview = findViewById(R.id.connection_state);
        drawer.addDrawerListener(ondrawerdraw);
        navelems.setNavigationItemSelectedListener(draweritemlistener);
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(getCurrentFocus(),getString(R.string.leave_app),Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.action_leave), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishAffinity();
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                drawer.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return false;
    }
    NavigationView.OnNavigationItemSelectedListener draweritemlistener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.main_nav_dashboard:{
                    vswitcher.setDisplayedChild(0);
                }
                case R.id.main_nav_profile:{
                    vswitcher.setDisplayedChild(1);
                }
                case R.id.main_nav_history:{
                    vswitcher.setDisplayedChild(2);
                }
            }
            return true;
        }
    };
}
