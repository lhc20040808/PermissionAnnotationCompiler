package com.marco.permission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.marco.permission_annotation.PermissionDenied;
import com.marco.permission_annotation.PermissionGrant;
import com.marco.permission_annotation.PermissionRational;
import com.marco.permission_helper.PermissionProxy;

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
    public void onPermissionGranted() {
        Toast.makeText(this, "权限申请成功", Toast.LENGTH_LONG).show();
    }

    @PermissionDenied(value = RESULT_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE)
    private void onPermissionDenied() {
        Toast.makeText(this, "权限申请被拒绝", Toast.LENGTH_LONG).show();
    }

    @PermissionRational(value = RESULT_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE)
    protected void onPermissionRational() {
        Toast.makeText(this, "弹出权限提示 ", Toast.LENGTH_LONG).show();
    }
}
