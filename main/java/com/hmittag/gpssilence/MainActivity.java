package com.hmittag.gpssilence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.hmittag.gpssilence.objects.BackgroundWorker;
import com.hmittag.gpssilence.objects.GlobalFileManager;
import com.hmittag.gpssilence.objects.Location;
import com.hmittag.gpssilence.objects.Settings;
import com.hmittag.gpssilence.objects.SwipeDismissTouchListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ////fields
    private static final String TAG ="MAIN";
    //views
    private NavigationView mNavView;
    private ConstraintLayout mClLocationDisclosure_Root;
    private TextView mTvLocationDisclosure;
    private Button mBtnClose_LocationDisclousre;
    private LinearLayout mLLWhitelist_Root;
    private ConstraintLayout mClWhitelist_Bg;
    private Button mBtnOkay_Whitelist;
    private Button mBtnCancel_Whitelist;
    //power
    private PowerManager mPowerManager;
    //file manager
    private GlobalFileManager mGlobalFileManager;
    //locations active
    private Settings mSettings = null;

    ////methods
    //lifecycle
    @Override
    protected void onStop() {
        Log.d(TAG, "on Stop");
        if (mSettings != null)    {
            if (mSettings.getLocationsActive()) {
                scheduleBackgroundWork();
            }
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on Destroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init views
        mNavView = findViewById(R.id.Main_nav_view);
        mClLocationDisclosure_Root = findViewById(R.id.Main_Location_Disclosure_Root);
        mTvLocationDisclosure = findViewById(R.id.Main_Tv_Location_Disclosure);
        mBtnClose_LocationDisclousre = findViewById(R.id.Main_Location_Disclosure_BtnClose);
        mLLWhitelist_Root = findViewById(R.id.Main_Whitelist_Warning_Root);
        mClWhitelist_Bg = findViewById(R.id.Main_Whitelist_Warning_Bg);
        mBtnOkay_Whitelist = findViewById(R.id.Main_Whitelist_Warning_BtnOk);
        mBtnCancel_Whitelist = findViewById(R.id.Main_Whitelist_Warning_BtnCancel);
        //init nav controller
        NavController navController = Navigation.findNavController(this, R.id.Main_Fragment);
        NavigationUI.setupWithNavController(mNavView, navController);
        //init global filemanager
        mGlobalFileManager = new GlobalFileManager(this);
        //init power manager
        mPowerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        //settings
        mSettings = mGlobalFileManager.readSettings();
        //location disclosure
        animateActivateDisclosure();
        //check doze whitelist
        if (!mPowerManager.isIgnoringBatteryOptimizations(this.getPackageName()))    {
            Log.d(TAG, "!is ignoring battery opti");
            animateActivateWhitelistWarning();
        }

    }

    //background
    public void scheduleBackgroundWork()  {
        Log.d(TAG, "trying to schedule job");
        PeriodicWorkRequest backgroundWorkRequest;
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(false).build();
        if (mSettings != null)  {
            backgroundWorkRequest = new PeriodicWorkRequest.Builder(BackgroundWorker.class, mSettings.getInterval(), TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build();
        }
        else {
            backgroundWorkRequest = new PeriodicWorkRequest.Builder(BackgroundWorker.class, 15, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build();
        }
        WorkManager.getInstance(getApplicationContext()).enqueue(backgroundWorkRequest);
    }


    //handle disclosure
    private void animateActivateDisclosure()    {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mNavView.animate().alpha(0.3f).setDuration(animation_time).setListener(null);

        mClLocationDisclosure_Root.setAlpha(0f);
        mClLocationDisclosure_Root.setVisibility(View.VISIBLE);
        mClLocationDisclosure_Root.animate().alpha(1f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mClLocationDisclosure_Root.setOnTouchListener(new SwipeDismissTouchListener(mClLocationDisclosure_Root, null,
                        new SwipeDismissTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(Object token) {
                                return true;
                            }

                            @Override
                            public void onDismiss(View view, Object token) {
                                mClLocationDisclosure_Root.setVisibility(View.GONE);
                                mNavView.setAlpha(1f);
                            }
                        }));
                mBtnClose_LocationDisclousre.setOnClickListener(v -> {
                    animateDeactivateDisclosure();
                });
            }
        });
    }

    private void animateDeactivateDisclosure()  {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mNavView.animate().alpha(1f).setDuration(animation_time).setListener(null);

        mClLocationDisclosure_Root.animate().alpha(0f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mClLocationDisclosure_Root.setVisibility(View.GONE);
            }
        });
    }

    //handle whitelist
    private void animateActivateWhitelistWarning()  {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mNavView.animate().alpha(0.3f).setDuration(animation_time).setListener(null);

        mClWhitelist_Bg.setVisibility(View.VISIBLE);
        mLLWhitelist_Root.setAlpha(0f);
        mLLWhitelist_Root.setVisibility(View.VISIBLE);
        mLLWhitelist_Root.animate().alpha(1f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                activateWhitelistWarning();
            }
        });
    }

    private void activateWhitelistWarning() {
        mBtnOkay_Whitelist.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
            animateDeactivateWhitelistWarning();
        });

        mBtnCancel_Whitelist.setOnClickListener(v -> {
            animateDeactivateWhitelistWarning();
        });
    }

    private void animateDeactivateWhitelistWarning()    {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mNavView.animate().alpha(1f).setDuration(animation_time).setListener(null);

        mLLWhitelist_Root.animate().alpha(0f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLLWhitelist_Root.setVisibility(View.GONE);
                mClWhitelist_Bg.setVisibility(View.GONE);
            }
        });
    }

}
