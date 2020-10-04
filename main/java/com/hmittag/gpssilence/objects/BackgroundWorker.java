package com.hmittag.gpssilence.objects;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.util.List;

public class BackgroundWorker extends Worker {

    ////fields
    private static final String TAG = "BackgroundWorker";
    private AudioManager mAudioManager;
    private GlobalFileManager mFileManager = null;
    private Settings mSettings = null;
    private List<Location> mLocations;
    //gps
    private static final long UPDATE_INTERVAL_GPS = 50000;
    private static final long FASTEST_UPDATE_INTERVAL_GPS = 25000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private android.location.Location mCurrentLocation = null;


    ////constructor
    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mFileManager = new GlobalFileManager(context);
        mLocations = mFileManager.readInAllLocations();
        mSettings = mFileManager.readSettings();
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }



    ////methods
    //doWork
    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "background work started");
        recieveCurrentLocation();
        if (mCurrentLocation != null) {
            Log.d(TAG, "current location not null");
            checkLocations();
        }
        Log.d(TAG, "background work finished");
        return Result.success();
    }

    //recieving current location
    private void recieveCurrentLocation() {
        Log.d(TAG, "recieving current location..");
        mLocationCallback = new LocationCallback()  {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_GPS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_GPS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
                @Override
                public void onComplete(@NonNull Task<android.location.Location> task) {
                    if (task.isSuccessful() && (task.getResult() != null))    {
                        mCurrentLocation = task.getResult();
                        Log.d(TAG, "successfully recieved location");
                        checkLocations();
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    }
                    else {
                        Log.d(TAG, "FAILED recieving current location");
                    }
                }
            });
        }
        catch (SecurityException e) {
            Log.d(TAG, "EXCEPTION: lost security permission" + e);
        }

        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, null);
        }
        catch (SecurityException e)    {
            Log.d(TAG, "EXCEPTION: lost security permission" + e);
        }

    }

    //check locations
    private void checkLocations()   {
        Log.d(TAG, "checking locations..");
        if (mLocations != null && !mLocations.isEmpty())    {
            boolean active = false;
            for (int i = 0; i < mLocations.size(); i++) {
                if (mLocations.get(i).checkForLocationInRadius(mCurrentLocation, mSettings.getRadius()))    {
                    adjustSoundProfile(mLocations.get(i).getSoundProfile());
                    active = true;
                    Log.d(TAG, "MATCH found, SP triggered");
                    break;
                }
            }
            if (!active) {
                adjustSoundProfile(new SoundProfile(mSettings.getDefaultSp()));
                Log.d(TAG, "default SP triggered");
            }
        }
        else {
            Log.d(TAG, "locations array maybe empty or null");
        }
    }

    //check and adjust soundprofile
    private void adjustSoundProfile(SoundProfile sound_profile)   {
        int flag = sound_profile.getCurrentFlag();
        switch (flag)   {
            case 0:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case 1:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case 2:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
        }
    }

}
