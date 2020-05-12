package com.marco.permission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.marco.permission_annotation.PermissionGrant;

public class MainActivity extends AppCompatActivity {
    private final static int RESULT_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @PermissionGrant(value = RESULT_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE)
    private void onRequestWriteExternalStorageGranted() {

    }

    @PermissionGrant(value = RESULT_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE)
    private void onRequestWriteExternalStorageDenied() {

    }
}
