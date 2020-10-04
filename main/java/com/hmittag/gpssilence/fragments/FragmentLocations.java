package com.hmittag.gpssilence.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.hmittag.gpssilence.R;
import com.hmittag.gpssilence.objects.CustomLinearLayout;
import com.hmittag.gpssilence.objects.GlobalFileManager;
import com.hmittag.gpssilence.objects.Location;
import com.hmittag.gpssilence.objects.SoundProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentLocations extends Fragment {
    ////fields
    //context
    private static final String TAG = "FragLocations";
    private Context mContext = null;
    //views
    private LinearLayout mLL_Root;
    private ConstraintLayout mCl_Options;
    private Button mBtnDelete;
    private Button mBtnEdit;
    private ConstraintLayout mCl_Edit;
    private TextView mEdit_TvName;
    private RadioGroup mEdit_RG;
    private RadioButton mEdit_RBtnMute;
    private RadioButton mEdit_RBtnVibrate;
    private RadioButton mEdit_RBtnLoud;
    private Button mEdit_BtnOk;
    private Button mEdit_BtnCancel;
    //Filemanager
    private GlobalFileManager mFileManager;
    //Locations
    private List<Location> mLocations = new ArrayList<>();
    //Autocomplete
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private List<Place.Field> mFields_AutoComplete;
    //list
    private boolean mClickOnListItem;
    //Intents
    private Intent mIntent_PlaceAutoComplete;

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
        Log.d(TAG, "onStop() called");
        super.onStop();
        mFileManager.overwriteLocations(mLocations);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    //onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locations, container, false);
        //init context
        mContext = root.getContext();
        //init file manager
        mFileManager = new GlobalFileManager(mContext);
        //init views
        mLL_Root = root.findViewById(R.id.Locations_ListContainer);
        mCl_Options = root.findViewById(R.id.Locations_ClOptions);
        mBtnDelete = root.findViewById(R.id.Locations_Options_BtnDelete);
        mBtnEdit = root.findViewById(R.id.Locations_Options_BtnEdit);
        mCl_Edit = root.findViewById(R.id.Locations_ClEdit);
        mEdit_TvName = root.findViewById(R.id.Locations_Edit_TvLbl);
        mEdit_RG = root.findViewById(R.id.Locations_Edit_RadioGroup);
        mEdit_RBtnMute = root.findViewById(R.id.Locations_Edit_radio_mute);
        mEdit_RBtnVibrate = root.findViewById(R.id.Locations_Edit_radio_vibrating);
        mEdit_RBtnLoud = root.findViewById(R.id.Locations_Edit_radio_loud);
        mEdit_BtnOk = root.findViewById(R.id.Locations_Edit_confirm);
        mEdit_BtnCancel = root.findViewById(R.id.Locations_Edit_cancel);
        //init locations
        initLocations();
        setupLocationListGUI();
        //setup auto complete
        mFields_AutoComplete = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        mIntent_PlaceAutoComplete = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, mFields_AutoComplete).build(mContext);

        return root;
    }

    //init Locations
    private void initLocations()    {
        mLocations = mFileManager.readInAllLocations();
    }
    //setup location list gui
    private void setupLocationListGUI() {
        if (mLocations != null && !mLocations.isEmpty()) {
            mLL_Root.removeAllViews();
            for (int i = 0; i < mLocations.size(); i++) {
                CustomLinearLayout ll = new CustomLinearLayout(mContext, i);
                ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ll.setOrientation(LinearLayout.HORIZONTAL);

                ll.setOnClickListener(v -> {
                    if (mClickOnListItem) {
                        animateDeactivateOptions();
                        mClickOnListItem = false;
                    }
                    else {
                        animateActivateOptions(ll.getId());
                        mClickOnListItem = true;
                    }
                });

                TextView tv = new TextView(mContext);
                LinearLayout.LayoutParams tv_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tv_params.gravity = Gravity.CENTER;
                tv_params.weight = 3;
                tv.setLayoutParams(tv_params);
                tv.setTextSize(24f);
                tv.setText(mLocations.get(i).getName());
                ll.addView(tv);

                ImageView iv = new ImageView(mContext);
                LinearLayout.LayoutParams iv_params = new LinearLayout.LayoutParams(150, 150);
                iv_params.gravity = Gravity.CENTER;
                tv_params.weight = 1;
                iv.setLayoutParams(iv_params);

                switch (mLocations.get(i).getSoundProfile().getCurrentFlag()) {
                    case 0:
                        iv.setBackgroundResource(R.drawable.ic_volume_off_black_24dp);
                        break;
                    case 1:
                        iv.setBackgroundResource(R.drawable.ic_vibration_black_24dp);
                        break;
                    case 2:
                        iv.setBackgroundResource(R.drawable.ic_volume_up_black_24dp);
                        break;
                }
                ll.addView(iv);
                mLL_Root.addView(ll);

                if (i != mLocations.size()-1) {
                    View divider = new View(mContext);
                    LinearLayout.LayoutParams divider_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10);
                    divider.setLayoutParams(divider_params);
                    divider.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBg_Grey));
                    mLL_Root.addView(divider);
                }
            }
        }
    }

    //handle options
    private void animateActivateOptions(int x)   {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mCl_Options.setVisibility(View.VISIBLE);
        mCl_Options.setAlpha(0f);
        mCl_Options.animate().alpha(1f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
               activateOptions(x);
            }
        });
    }

    private void activateOptions(int x)  {
        mBtnDelete.setOnClickListener(v -> {
            mLocations.remove(x);
            setupLocationListGUI();
            animateDeactivateOptions();
        });

        mBtnEdit.setOnClickListener(v -> {
            mEdit_TvName.setText(mLocations.get(x).getName());
            switch (mLocations.get(x).getSoundProfile().getCurrentFlag())   {
                case 0:
                    mEdit_RG.check(R.id.Locations_Edit_radio_mute);
                    break;
                case 1:
                    mEdit_RG.check(R.id.Locations_Edit_radio_vibrating);
                    break;
                case 2:
                    mEdit_RG.check(R.id.Locations_Edit_radio_loud);
                    break;
            }

            animateDeactivateOptions();
            animateActivateEdit(x);
        });
    }

    private void animateDeactivateOptions() {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mCl_Options.animate().alpha(0f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCl_Options.setVisibility(View.GONE);
            }
        });
    }

    //handle edit
    private void animateActivateEdit(int x)  {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mCl_Edit.setVisibility(View.VISIBLE);
        mCl_Edit.setAlpha(0f);
        mCl_Edit.animate().alpha(1f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                activateEdit(x);
            }
        });

    }

    private void activateEdit(int x) {
        mEdit_BtnOk.setOnClickListener(v -> {
            if (!(mEdit_RG.getCheckedRadioButtonId() == -1))   {
                mLocations.set(x, new Location(mLocations.get(x).getLatLng(), mLocations.get(x).getName(), getSoundProfileEdit()));
                animateDeactivateEdit();
            }
            else {
                Toast.makeText(mContext, R.string.exception_notChecked, Toast.LENGTH_SHORT).show();
            }
        });

        mEdit_BtnCancel.setOnClickListener(v -> {
            animateDeactivateEdit();
        });
    }

    private void animateDeactivateEdit()    {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mCl_Edit.animate().alpha(0f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCl_Edit.setVisibility(View.GONE);
            }
        });
    }

    //get sound profile flag from edit gui
    private SoundProfile getSoundProfileEdit()   {
        int flag = 0;
        int alarm_vol = 0;
        if (mEdit_RBtnMute.isChecked())   {
            flag = 0;
        }
        else {
            if (mEdit_RBtnVibrate.isChecked())    {
                flag = 1;
            }
            else {
                if (mEdit_RBtnLoud.isChecked())    {
                    flag = 2;
                }
            }
        }
        return new SoundProfile(flag);
    }

}
