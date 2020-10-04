package com.hmittag.gpssilence.objects;

public class SoundProfile {
    ////fields
    private int mCurrent_Flag = 0;

    private static final int MUTE_FLAG = 0;
    private static final int VIBRATE_FLAG = 1;
    private static final int LOUD_FLAG = 2;
    ////constructor
    public SoundProfile()   {
        this.mCurrent_Flag = 1;
    }
    public SoundProfile(int sound_flag) {
        this.mCurrent_Flag = sound_flag;
    }
    ////methods
    //getter setter
    public static int getMuteFlag() {
        return MUTE_FLAG;
    }
    public static int getVibrateFlag() {
        return VIBRATE_FLAG;
    }
    public static int getLoudFlag() {
        return LOUD_FLAG;
    }

    public int getCurrentFlag() {
        return this.mCurrent_Flag;
    }
    public void setCurrentFlag(int current_flag)    {
        this.mCurrent_Flag = current_flag;
    }

}
