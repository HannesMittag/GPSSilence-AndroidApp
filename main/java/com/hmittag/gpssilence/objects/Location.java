package com.hmittag.gpssilence.objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Location {
    ////fields
    private LatLng mLatLng = null;
    private String mName = null;
    private SoundProfile mSoundProfile = null;
    ////constructor
    public Location(LatLng latlng)  {
        this.mLatLng = latlng;
    }
    public Location(LatLng latlng, String name)  {
        this.mLatLng = latlng;
        this.mName = name;
    }
    public Location(LatLng latlng, String name, SoundProfile sound_profile)  {
        this.mLatLng = latlng;
        this.mName = name;
        this.mSoundProfile = sound_profile;
    }
    ////methods
    //getter set
    public void setLatLng(LatLng latlng)    {
        this.mLatLng = latlng;
    }
    public LatLng getLatLng()    {
        return this.mLatLng;
    }

    public void setName(String name)    {
        this.mName = name;
    }
    public String getName()    {
        return this.mName;
    }

    public void setSoundProfile(SoundProfile sp)    {
        this.mSoundProfile = sp;
    }
    public SoundProfile getSoundProfile()   {
        return this.mSoundProfile;
    }

    //equals Location
    public boolean equalsLocation(Location loc) {
        if (this.getLatLng().latitude == loc.getLatLng().latitude) {
            if (this.getLatLng().longitude == loc.getLatLng().longitude)    {
                if (this.mName.equals(loc.getName()))   {
                    return true;
                }
            }
        }
        return false;
    }

    //check for location in radius (meters)
    public boolean checkForLocationInRadius(android.location.Location loc_b, float radius) {
        android.location.Location loc_a = new android.location.Location("");
        loc_a.setLatitude(this.getLatLng().latitude);
        loc_a.setLongitude(this.getLatLng().longitude);
        if (loc_a.distanceTo(loc_b) <= radius)  {
            return true;
        }
        return false;
    }

    ////static methods
    //convert string to location
    public static Location stringToLocation(String in)    {
        double lat = 0;
        double lng = 0;
        String name = null;
        int flag = 0;
        int alarm_vol = 0;
        String[] sliced = AdvancedString.slice(in, '%');
        lat = Double.parseDouble(sliced[0]);
        lng = Double.parseDouble(sliced[1]);
        name = sliced[2];
        flag = Integer.parseInt(sliced[3]);

        if (name == null)   {
            return null;
        }
        return new Location(new LatLng(lat,lng), name, new SoundProfile(flag));
    }

    //convert location to string
    public static String locationToString(Location loc) {
        String ret = "";
        ret += String.valueOf(loc.getLatLng().latitude);
        ret += "%";
        ret += String.valueOf(loc.getLatLng().longitude);
        ret += "%";
        ret += loc.getName();
        ret += "%";
        ret += String.valueOf(loc.getSoundProfile().getCurrentFlag());
        return ret;
    }

    //toString


    @NonNull
    @Override
    public String toString() {
        return this.mName;
    }
}
