package com.hmittag.gpssilence;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    ////fields
    private static final String TAG = "Splash";
    //views
    private ConstraintLayout mClRoot;
    private ImageView mIv_Logo;
    private ConstraintLayout mClPermissionLocationRoot;
    private Button mBtnPermissionLocationOK;
    private Button mBtnPermissionLocationClose;
    private ConstraintLayout mClPermissionSoundRoot;
    private Button mBtnPermissionSoundOk;
    private Button mBtnPermissionSoundClose;

    private ConstraintLayout mClPermissionDeniedRoot;
    private Button mBtnPermissionDeniedOkay;
    //permissions
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 200;
    private static final int REQUEST_CODE_PERMISSION_NOTIFICATION_POLICY = 201;
    //Intents
    private Intent mIntent_Main;

    ////methods
    //activity result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSION_NOTIFICATION_POLICY) {
            if (checkNotificationPolicyAccess()) {
                Log.d(TAG, "Notification_Policy: GRANTED");
                startActivity(mIntent_Main);
            } else {
                Log.d(TAG, "Notification_Policy: DENIED");
                animateDeactiveSoundPermissionUI();
                animateActivatePermissionDeniedUI();
            }
        }
    }

    //permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)    {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                    animateDeactivateLocationPermissionUI(true);
                }
                else {
                    animateDeactivateLocationPermissionUI(false);
                    animateActivatePermissionDeniedUI();
                }

        }
    }

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //init views
        this.mClRoot = findViewById(R.id.Splash_Root);
        this.mIv_Logo = findViewById(R.id.Splash_Logo);
        this.mClPermissionLocationRoot = findViewById(R.id.Splash_Permission_Location_Root);
        this.mBtnPermissionLocationOK = findViewById(R.id.Splash_permission_Location_BtnAccept);
        this.mBtnPermissionLocationClose = findViewById(R.id.Splash_permission_Location_BtnClose);
        this.mClPermissionDeniedRoot = findViewById(R.id.Splash_permission_Location_denied_root);
        this.mBtnPermissionDeniedOkay = findViewById(R.id.Splash_permission_Location_denied_BtnAccept);
        this.mClPermissionSoundRoot = findViewById(R.id.Splash_Permission_Sound_Root);
        this.mBtnPermissionSoundOk = findViewById(R.id.Splash_permission_Sound_BtnAccept);
        this.mBtnPermissionSoundClose = findViewById(R.id.Splash_permission_Sound_BtnClose);
        //init Intents
        this.mIntent_Main = new Intent(this, MainActivity.class);
        //timer
        new CountDownTimer(1000, 1000)  {

            public void onTick(long millisUntilFinished)    {

            }

            public void onFinish()  {
                checkPermissions();
            }
        }.start();
    }

    //check permission
    private void checkPermissions()  {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (checkNotificationPolicyAccess())    {
                startActivity(mIntent_Main);
            }
            else {
                animateActivateSoundPermissionUI();
            }
        }
        else {
            animateActivateLocationPermissionUI();
        }


    }

    private boolean checkNotificationPolicyAccess()    {
        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
            return false;
        }
        return true;
    }

    //permission location ui
    private void animateActivateLocationPermissionUI()  {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mClRoot.animate().alpha(0.3f).setDuration(animation_time).setListener(null);

        mClPermissionLocationRoot.setAlpha(0f);
        mClPermissionLocationRoot.setVisibility(View.VISIBLE);
        mClPermissionLocationRoot.animate().alpha(1f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                activateLocationPermissionUI();
            }
        });

    }

    private void animateDeactivateLocationPermissionUI(boolean granted)    {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);
        if (granted)    {
            if (checkNotificationPolicyAccess())    {
                startActivity(mIntent_Main);
            }
            else {
                mClRoot.animate().alpha(1f).setDuration(animation_time).setListener(null);

                mClPermissionLocationRoot.animate().alpha(0f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mClPermissionLocationRoot.setVisibility(View.GONE);
                    }
                });
                animateActivateSoundPermissionUI();
            }
        }
        else {
            mClRoot.animate().alpha(1f).setDuration(animation_time).setListener(null);

            mClPermissionLocationRoot.animate().alpha(0f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mClPermissionLocationRoot.setVisibility(View.GONE);
                }
            });
        }
    }

    private void activateLocationPermissionUI() {
        mBtnPermissionLocationOK.setOnClickListener(v -> {
            Log.d("Permission", "requesting permission");
            String[] perms = {"android.permission.ACCESS_FINE_LOCATION"};
            ActivityCompat.requestPermissions(this, perms, REQUEST_CODE_PERMISSION_LOCATION);
        });

        mBtnPermissionLocationClose.setOnClickListener(v -> {
            animateDeactivateLocationPermissionUI(false);
            animateActivatePermissionDeniedUI();
        });
    }

    //permission sound ui
    private void animateActivateSoundPermissionUI() {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mClRoot.animate().alpha(0.3f).setDuration(animation_time).setListener(null);

        mClPermissionSoundRoot.setAlpha(0f);
        mClPermissionSoundRoot.setVisibility(View.VISIBLE);
        mClPermissionSoundRoot.animate().alpha(1f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                activateSoundPermissionUI();
            }
        });
    }

    private void activateSoundPermissionUI()    {
        mBtnPermissionSoundOk.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUEST_CODE_PERMISSION_NOTIFICATION_POLICY);
        });

        mBtnPermissionSoundClose.setOnClickListener(v -> {
            animateDeactiveSoundPermissionUI();
            animateActivatePermissionDeniedUI();
        });
    }

    private void animateDeactiveSoundPermissionUI()  {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mClRoot.animate().alpha(1f).setDuration(animation_time).setListener(null);

        mClPermissionSoundRoot.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mClPermissionSoundRoot.setVisibility(View.GONE);
            }
        });
    }

    //denied permission ui
    private void animateActivatePermissionDeniedUI()    {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mClRoot.animate().alpha(0.3f).setDuration(animation_time).setListener(null);

        mClPermissionDeniedRoot.setAlpha(0f);
        mClPermissionDeniedRoot.setVisibility(View.VISIBLE);
        mClPermissionDeniedRoot.animate().alpha(1f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBtnPermissionDeniedOkay.setOnClickListener(v -> {
                    recreate();
                });
            }
        });
    }


}
