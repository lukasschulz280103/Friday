package com.friday.ar.ui.store.packageInstaller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.friday.ar.R;
import com.friday.ar.dialog.store.packageInstaller.PackageInstallerDialog;

public class PackageInstallerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_installer);
        PackageInstallerDialog packageInstallerDialog = new PackageInstallerDialog();
        packageInstallerDialog.show(getSupportFragmentManager(), "PackageInstallerDialog");
    }
}
