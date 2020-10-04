package com.hmittag.gpssilence.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.hmittag.gpssilence.R;
import com.hmittag.gpssilence.objects.GlobalFileManager;
import com.hmittag.gpssilence.objects.Settings;

public class FragmentSettings extends Fragment {
    ////fields
    private static final String TAG = "SETTINGS";
    private Context mContext;
    //views
    private SeekBar mSbInterval;
    private SeekBar mSbRadius;
    private TextView mTvInterval;
    private TextView mTvRadius;
    private Button mBtnBack;
    private LinearLayout mLLDefaultSp;
    //Filemanager
    private GlobalFileManager mFilemanager = null;
    //values
    private Settings mSettings = null;

    ////methods
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        updateSettings();
        mFilemanager.saveSettings(mSettings);
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        updateSettings();
        mFilemanager.saveSettings(mSettings);
        super.onDestroy();
    }

    //onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        //init context
        mContext = root.getContext();
        //init views
        mSbInterval = root.findViewById(R.id.Settings_IntervalSlider);
        mSbRadius = root.findViewById(R.id.Settings_RadiusSlider);
        mTvInterval = root.findViewById(R.id.Settings_Interval);
        mTvRadius = root.findViewById(R.id.Settings_Radius);
        mLLDefaultSp = root.findViewById(R.id.Settings_LLDefaultSp_Buttons);
        //init filemanager
        mFilemanager = new GlobalFileManager(root.getContext());
        //settings
        initSettings();
        //handle interval
        mSbInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvInterval.setText(String.valueOf(progress) + " minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //handle radius
        mSbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvRadius.setText(String.valueOf(progress) + " meter");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return root;
    }

    //handle settings
    private void initSettings() {
        mSettings = mFilemanager.readSettings();
        if (mSettings == null) {
            mSettings = new Settings();
        }
        initDefaultSp();
        mTvRadius.setText(String.valueOf(mSettings.getRadius()) +" meter");
        mSbRadius.setProgress(mSettings.getRadius());

        mTvInterval.setText(String.valueOf(mSettings.getInterval())+" minutes");
        mSbInterval.setProgress(mSettings.getInterval());
    }

    private void initDefaultSp()    {
        Log.d(TAG, "init defaultSP called with defaultSP = " + mSettings.getDefaultSp());
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLLDefaultSp.animate().alpha(0f).setDuration(animation_time).setListener(null);
        mLLDefaultSp.removeAllViews();
        int button_width = 160;
        int button_height = 160;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(button_width,button_height);
        params.gravity = Gravity.CENTER;
        switch (mSettings.getDefaultSp())    {
            case 0:
                Button btnVibrate = new Button(mContext);
                btnVibrate.setBackgroundResource(R.drawable.ic_vibration_black_24dp);
                btnVibrate.setLayoutParams(params);
                btnVibrate.setOnClickListener(v -> {
                    mSettings.setDefaultSp(1);
                    initDefaultSp();
                });

                Button btnMute = new Button(mContext);
                btnMute.setBackgroundResource(R.drawable.ic_volume_off_mint_24dp);
                btnMute.setLayoutParams(params);
                btnMute.setOnClickListener(v -> {
                    mSettings.setDefaultSp(0);
                    initDefaultSp();
                });

                Button btnLoud = new Button(mContext);
                btnLoud.setBackgroundResource(R.drawable.ic_volume_up_black_24dp);
                btnLoud.setLayoutParams(params);
                btnLoud.setOnClickListener(v -> {
                    mSettings.setDefaultSp(2);
                    initDefaultSp();
                });

                mLLDefaultSp.addView(btnVibrate);
                mLLDefaultSp.addView(btnMute);
                mLLDefaultSp.addView(btnLoud);
                mLLDefaultSp.animate().alpha(1f).setDuration(animation_time).setListener(null);
                break;
            case 1:
                Button btnVibrate1 = new Button(mContext);
                btnVibrate1.setBackgroundResource(R.drawable.ic_vibration_mint_24dp);
                btnVibrate1.setLayoutParams(params);
                btnVibrate1.setOnClickListener(v -> {
                    mSettings.setDefaultSp(1);
                    initDefaultSp();
                });

                Button btnMute1 = new Button(mContext);
                btnMute1.setBackgroundResource(R.drawable.ic_volume_off_black_24dp);
                btnMute1.setLayoutParams(params);
                btnMute1.setOnClickListener(v -> {
                    mSettings.setDefaultSp(0);
                    initDefaultSp();
                });

                Button btnLoud1 = new Button(mContext);
                btnLoud1.setBackgroundResource(R.drawable.ic_volume_up_black_24dp);
                btnLoud1.setLayoutParams(params);
                btnLoud1.setOnClickListener(v -> {
                    mSettings.setDefaultSp(2);
                    initDefaultSp();
                });

                mLLDefaultSp.addView(btnMute1);
                mLLDefaultSp.addView(btnVibrate1);
                mLLDefaultSp.addView(btnLoud1);
                mLLDefaultSp.animate().alpha(1f).setDuration(animation_time).setListener(null);
                break;
            case 2:
                Button btnVibrate2 = new Button(mContext);
                btnVibrate2.setBackgroundResource(R.drawable.ic_vibration_black_24dp);
                btnVibrate2.setLayoutParams(params);
                btnVibrate2.setOnClickListener(v -> {
                    mSettings.setDefaultSp(1);
                    initDefaultSp();
                });

                Button btnMute2 = new Button(mContext);
                btnMute2.setBackgroundResource(R.drawable.ic_volume_off_black_24dp);
                btnMute2.setLayoutParams(params);
                btnMute2.setOnClickListener(v -> {
                    mSettings.setDefaultSp(0);
                    initDefaultSp();
                });

                Button btnLoud2 = new Button(mContext);
                btnLoud2.setBackgroundResource(R.drawable.ic_volume_up_mint_24dp);
                btnLoud2.setLayoutParams(params);
                btnLoud2.setOnClickListener(v -> {
                    mSettings.setDefaultSp(2);
                    initDefaultSp();
                });

                mLLDefaultSp.addView(btnMute2);
                mLLDefaultSp.addView(btnLoud2);
                mLLDefaultSp.addView(btnVibrate2);
                mLLDefaultSp.animate().alpha(1f).setDuration(animation_time).setListener(null);
                break;
            }

    }

    //update Settings
    private void updateSettings()   {
        mSettings.setInterval(mSbInterval.getProgress());
        mSettings.setRadius(mSbRadius.getProgress());

    }

}
