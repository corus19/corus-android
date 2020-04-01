package com.jamdoli.corus.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import com.jamdoli.corus.R;
import com.jamdoli.corus.utils.AppSettingsUsingSharedPrefs;
import com.jamdoli.corus.utils.DebugLogManager;
import com.jamdoli.corus.utils.MyAppContext;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class IntroActivity extends AppCompatActivity {

    private final static String LOG_TAG = "IntroActivity:: ";
    Button btEnable;
    DebugLogManager logManager;
    BluetoothAdapter mBluetoothAdapter;
    AppSettingsUsingSharedPrefs appSettingsUsingSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        MyAppContext.setInstance("IntroActivity", this);
        logManager = DebugLogManager.getInstance();
        appSettingsUsingSharedPrefs = AppSettingsUsingSharedPrefs.getInstance();

        BluetoothManager manager = (BluetoothManager) MyAppContext.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        logManager.logsForDebugging(LOG_TAG,"isEnabled: "+mBluetoothAdapter.isEnabled()+"");

        btEnable = findViewById(R.id.intro_bt_enable);
        if (checkIsPermmissionEnabled()) {
            nextScreen();
        }

        btEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionGen.with(IntroActivity.this)
                        .addRequestCode(100)
                        .permissions(
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                        .request();
            }
        });
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 100);
        } else {
            nextScreen();
        }

        logManager.logsForDebugging(LOG_TAG, mBluetoothAdapter.getBondedDevices().toString());
        appSettingsUsingSharedPrefs.setBtAddress(android.provider.Settings.Secure.getString(MyAppContext.getInstance().getContentResolver(), "bluetooth_address"));

    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        //     Toast.makeText(this, "Contact permission is not granted", Toast.LENGTH_SHORT).show();
        logManager.logsForDebugging(getClass().getName(), "onRequestPermissionsResult()   need");
        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.enable_settings),
                Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.enable),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                nextScreen();
            }
        }
    }

    public void nextScreen() {
        if (appSettingsUsingSharedPrefs.getIsUserLogin()) {
            startActivity(new Intent(IntroActivity.this, HomeActivity.class));
        } else {
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        }

    }

    public boolean checkIsPermmissionEnabled() {
        if (ContextCompat.checkSelfPermission(MyAppContext.getInstance(),
                Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MyAppContext.getInstance(),
                Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MyAppContext.getInstance(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MyAppContext.getInstance(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (mBluetoothAdapter.isEnabled()) {
                return true;
            } else return false;
        }
        return false;
    }

}
