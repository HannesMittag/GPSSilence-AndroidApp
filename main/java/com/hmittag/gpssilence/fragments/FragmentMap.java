package com.hmittag.gpssilence.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.hmittag.gpssilence.BuildConfig;
import com.hmittag.gpssilence.MainActivity;
import com.hmittag.gpssilence.R;
import com.hmittag.gpssilence.objects.GlobalFileManager;
import com.hmittag.gpssilence.objects.Settings;
import com.hmittag.gpssilence.objects.SoundProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentMap extends Fragment implements OnMapReadyCallback {
    ////fields
    private Context mContext = null;
    private static final String TAG = "Map";
    //views
    private MapView mMapView;
    private Button mBtnAdd;
    private ConstraintLayout mClAdd_Root;
    private EditText mEtAdd_Lbl;
    private RadioGroup mRgAdd;
    private RadioButton mRbAdd_Loud;
    private RadioButton mRb_Add_Vibrate;
    private RadioButton mRb_Add_Mute;
    private Button mBtnAdd_Ok;
    private Button mBtnAdd_Cancel;
    //map
    private final String MAP_VIEW_BUNDLE_KEY = "Map01";
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLocation_Now = null;
    //saved locations
    private List<com.hmittag.gpssilence.objects.Location> mLocations;
    //add
    private LatLng mLatLng_Add = null;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private List<Place.Field> mFields_AutoComplete;
    //File Manager
    private GlobalFileManager mFileManager = null;
    //audio
    private AudioManager mAudioManger;
    //Intents
    private Intent mIntent_PlaceAutoComplete;

    ////methods
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMap(mMap);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        initSavedLocations();
        for (int i = 0; i < mLocations.size(); i++) {
            Log.d(TAG, mLocations.get(i).toString());
        }
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                mLatLng_Add = place.getLatLng();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                setupAddGUI(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d("AutoComplete", status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("AutoComplete", "user canceled");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mContext = root.getContext();
        //init views
        mMapView = root.findViewById(R.id.Map_map);
        mBtnAdd = root.findViewById(R.id.Map_BtnAdd);
        mClAdd_Root = root.findViewById(R.id.Map_Add_root);
        mEtAdd_Lbl = root.findViewById(R.id.Map_add_EtLbl);
        mRgAdd = root.findViewById(R.id.Map_add_RadioGroup);
        mRbAdd_Loud = root.findViewById(R.id.Map_add_radio_loud);
        mRb_Add_Vibrate = root.findViewById(R.id.Map_add_radio_vibrating);
        mRb_Add_Mute = root.findViewById(R.id.Map_add_radio_mute);
        mBtnAdd_Ok = root.findViewById(R.id.Map_add_confirm);
        mBtnAdd_Cancel = root.findViewById(R.id.Map_add_cancel);
        //init global filemanager
        this.mLocations = new ArrayList<>();
        this.mFileManager = new GlobalFileManager(root.getContext());
        //init audio manager
        this.mAudioManger = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //init mapview
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        //index locations
        initSavedLocations();
        setupLocations();
        //setup auto complete
        Places.initialize(mContext.getApplicationContext(), BuildConfig.API_KEY);
        mFields_AutoComplete = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
        mIntent_PlaceAutoComplete = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, mFields_AutoComplete).build(mContext);
        mBtnAdd.setOnClickListener(v -> {
            startActivityForResult(mIntent_PlaceAutoComplete, AUTOCOMPLETE_REQUEST_CODE);
        });

        return root;
    }


    //setup map
    private void setupMap(GoogleMap map) {
        //LatLngBounds
        LatLngBounds GERMANY = new LatLngBounds(new LatLng(47.2563, 5.9369), new LatLng(54.9329, 14.7261));
        map.setLatLngBoundsForCameraTarget(GERMANY);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mLatLng_Add = latLng;
                animateActivateAdd();
            }
        });
    }

    //init saved locations
    private void initSavedLocations() {
        this.mLocations = mFileManager.readInAllLocations();
    }

    //setup locations and map
    private void setupLocations() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        try {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mLocation_Now = location;
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLocation_Now.getLatitude(), mLocation_Now.getLongitude())));
                        if (mLocations != null) {
                            for (int i = 0; i < mLocations.size(); i++) {
                                mMap.addMarker(new MarkerOptions().position(mLocations.get(i).getLatLng()).title(mLocations.get(i).getName()));
                            }
                        }
                    } else {
                        Toast.makeText(mContext, R.string.exception_location, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }


    }


    //handle add directly on map
    private void animateActivateAdd()   {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mMapView.animate().alpha(0.3f).setDuration(animation_time).setListener(null);

        mClAdd_Root.setAlpha(0f);
        mClAdd_Root.setVisibility(View.VISIBLE);
        mClAdd_Root.animate().alpha(1f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                activateAdd();
            }
        });
    }

    private void activateAdd()  {
        mBtnAdd_Ok.setOnClickListener(v -> {
            if (!mEtAdd_Lbl.getText().toString().equals("")) {
                if (!(mRgAdd.getCheckedRadioButtonId() == -1)) {
                    //mLocations = new ArrayList<>();
                    mLocations.add(new com.hmittag.gpssilence.objects.Location(mLatLng_Add, mEtAdd_Lbl.getText().toString(), getSoundProfileAdd()));
                    mMap.addMarker(new MarkerOptions().position(mLatLng_Add).title(mEtAdd_Lbl.getText().toString()));
                    mFileManager.saveLocation(mLocations.get(mLocations.size() - 1));

                    animateDeactivateAdd();
                }
                else {
                    Toast.makeText(mContext, R.string.exception_notChecked, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(mContext, R.string.exception_emptyLbl, Toast.LENGTH_SHORT).show();
            }
        });


        mBtnAdd_Cancel.setOnClickListener(v -> {
            mEtAdd_Lbl.setText("");
            animateDeactivateAdd();
        });
    }

    private void animateDeactivateAdd() {
        long animation_time = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mClAdd_Root.animate().alpha(0f).setDuration(animation_time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mEtAdd_Lbl.setText("");
                mRb_Add_Mute.setChecked(false);
                mRb_Add_Vibrate.setChecked(false);
                mRbAdd_Loud.setChecked(false);
                mClAdd_Root.setVisibility(View.GONE);
            }
        });

        mMapView.animate().alpha(1f).setDuration(animation_time).setListener(null);



    }

    private void setupAddGUI(String name)   {
        mEtAdd_Lbl.setText(name);
        animateActivateAdd();
    }

    //get sound profile flag from add gui
    private SoundProfile getSoundProfileAdd()   {
        int flag = 0;
        if (mRb_Add_Mute.isChecked())   {
            flag = 0;
        }
        else {
            if (mRb_Add_Vibrate.isChecked())    {
                flag = 1;
            }
            else {
                if (mRbAdd_Loud.isChecked())    {
                    flag = 2;
                }
            }
        }
        return new SoundProfile(flag);
    }

}
