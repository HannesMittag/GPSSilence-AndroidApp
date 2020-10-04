package com.hmittag.gpssilence.objects;

public class Settings {
    ////fields
    private boolean mLocationsActive;
    private int mInterval;
    private int mRadius;
    private int mDefaultSoundFlag;

    ////constructor
    public Settings()   {
        this.mLocationsActive = false;
        this.mInterval = 15;
        this.mRadius = 50;
        this.mDefaultSoundFlag = 1;
    }
    public Settings(boolean locations_active)    {
        this.mLocationsActive = locations_active;
        this.mDefaultSoundFlag = 1;                                                                 //default
    }
    public Settings(boolean locations_active, int interval, int radius)  {
        this.mLocationsActive = locations_active;
        this.mInterval = interval;
        this.mRadius = radius;
        this.mDefaultSoundFlag = 1;                                                                 //default
    }
    public Settings(boolean locations_active, int interval, int radius, int default_sound_flag)  {
        this.mLocationsActive = locations_active;
        this.mInterval = interval;
        this.mRadius = radius;
        this.mDefaultSoundFlag = default_sound_flag;
    }

    ////methods
    //getter setter
    public boolean getLocationsActive()  {
        return this.mLocationsActive;
    }

    public void setLocationsActive(boolean locations_active)    {
        this.mLocationsActive = locations_active;
    }

    public int getInterval()   {
        return this.mInterval;
    }

    public void setInterval(int interval)  {
        this.mInterval = interval;
    }

    public int getRadius()  {
        return this.mRadius;
    }

    public void setRadius(int radius)   {
        this.mRadius = radius;
    }

    public int getDefaultSp()   {
        return this.mDefaultSoundFlag;
    }

    public void setDefaultSp(int flag)  {
        this.mDefaultSoundFlag = flag;
    }
}
