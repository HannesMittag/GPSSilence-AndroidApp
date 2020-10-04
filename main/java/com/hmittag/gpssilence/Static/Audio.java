package com.hmittag.gpssilence.Static;

import android.content.Context;
import android.media.AudioManager;

import com.hmittag.gpssilence.objects.Location;

import java.util.List;

public class Audio {
    //functions
    public static void manageAudio(AudioManager am, List<Location> locations)    {
        for (int i = 0; i < locations.size(); i++)  {
            switch (locations.get(i).getSoundProfile().getCurrentFlag())    {
                case 0:
                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    break;
                case 1:
                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
                case 2:
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    break;
            }
        }
    }
}
