package com.hmittag.gpssilence.objects;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class GlobalFileManager {
    //global variables
    private static final String SETTINGS_FILE = "settings.txt";                                     //settings_file:
                                                                                                    //$locations active[\n]
                                                                                                    //$frequency[\n]
                                                                                                    //$radius[\n]
                                                                                                    //$defaultSp[\n]
    private static final String LOCATION_FILE = "locations.txt";

    //fields
    private Context mContext = null;
    private File mRootDir = null;

    //constructor
    public GlobalFileManager(Context c)  {
        this.mContext = c;
        this.mRootDir = new File(c.getExternalFilesDir(null).getAbsolutePath());
    }

    ////methods
    //check external storage
    public boolean isExternalStorageWritable()  {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))    {
            return true;
        }
        else {
            return false;
        }
    }

    //read in all locations
    public List<Location> readInAllLocations()    {
        if (isExternalStorageWritable())    {
            try {
                List<Location> locations_list = new ArrayList<>();
                FileInputStream fis = mContext.getApplicationContext().openFileInput(LOCATION_FILE);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null)  {
                    locations_list.add(Location.stringToLocation(line));
                }
                br.close();
                Log.d("FileManager", "read in all locations");
                return locations_list;
            }
            catch (IOException e)   {
                Log.d("FileManager", "FAILED read in all locations");
                return null;
            }
        }
        else {
            Log.d("FileManager", "FAILED read in all locations");
            return null;
        }
    }

    //save location
    public boolean saveLocation(Location loc)   {
        if (isExternalStorageWritable())    {
            if (loc != null)    {
                try {
                    FileOutputStream fos = mContext.getApplicationContext().openFileOutput(LOCATION_FILE, Context.MODE_APPEND);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write(Location.locationToString(loc)+"\n");
                    osw.close();
                    Log.d("FileManager", "saved Location");
                    return true;
                }
                catch (IOException e)   {
                    Log.d("FileManager", "FAILED SAVE LOCATION");
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    //overwrite all locations
    public boolean overwriteLocations(List<Location> locations) {
        if (locations != null && !locations.isEmpty())  {
            if (isExternalStorageWritable())    {
                try {
                    FileOutputStream fos = mContext.getApplicationContext().openFileOutput(LOCATION_FILE, Context.MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    for (int i = 0; i < locations.size(); i++)  {
                        osw.write(Location.locationToString(locations.get(i))+"\n");
                    }
                    osw.close();
                    Log.d("FileManager", "saved Locations");
                    return true;
                }
                catch (IOException e)   {
                    e.printStackTrace();
                    Log.d("FileManager", "FAILED SAVE LOCATIONS");
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return false;
    }


    //delete Location
    public boolean deleteLocation(Location loc) {
        if (isExternalStorageWritable())    {
            if (loc != null)    {
                List<Location> list_locations = readInAllLocations();
                if (list_locations != null) {
                    for (int i = 0; i < list_locations.size(); i++) {
                        if (list_locations.get(i).equalsLocation(loc))  {
                            list_locations.remove(i);
                            break;
                        }
                    }
                    try {
                        FileOutputStream fos = mContext.getApplicationContext().openFileOutput(LOCATION_FILE, Context.MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fos);
                        for (int i = 0; i < list_locations.size(); i++) {
                            osw.write(Location.locationToString(list_locations.get(i))+"\n");
                        }
                        osw.close();
                        Log.d("FileManager", "delete location successfull");
                        return true;
                    }
                    catch (IOException e)   {
                        Log.d("FileManager", "FAILED DELETE LOCATION");
                        return false;
                    }
                }
                else {
                    return false;
                }

            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    //handle settings
    public boolean saveSettings(Settings s) {
        if (isExternalStorageWritable())    {
            try {
                FileOutputStream fos = mContext.getApplicationContext().openFileOutput(SETTINGS_FILE, Context.MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.write(String.valueOf(s.getLocationsActive() + "\n" + s.getInterval()) + "\n" + s.getRadius() + "\n" + s.getDefaultSp() + "\n");
                osw.close();
                Log.d("FileManager", "Settings - save successfull");
                return true;
            }
            catch (IOException e)   {
                Log.d("FileManager", "Settings - couldnt save");
                return false;
            }
        }
        else {
            Log.d("FileManager", "Settings - couldnt save");
            return false;
        }
    }

    public Settings readSettings()  {
        if (isExternalStorageWritable())    {
            try {
                FileInputStream fis = mContext.getApplicationContext().openFileInput(SETTINGS_FILE);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null)  {
                    lines.add(line);
                }
                br.close();

                switch (lines.size())   {
                    case 0:
                        return null;
                    case 1:
                        return new Settings(Boolean.parseBoolean(lines.get(0)));
                    case 2:

                }
                if (lines.size() != 0)  {
                    if (lines.size() > 1)   {
                        return new Settings(Boolean.parseBoolean(lines.get(0)), Integer.parseInt(lines.get(1)), Integer.parseInt(lines.get(2)));
                    }
                    else {
                        return new Settings(Boolean.parseBoolean(lines.get(0)));
                    }
                }

                return null;
            }
            catch (IOException e)   {
                Log.d("FileManager", "Settings - couldnt read");
                return null;
            }
        }
        else {
            Log.d("FileManager", "Settings - couldnt read");
            return null;
        }
    }
}
