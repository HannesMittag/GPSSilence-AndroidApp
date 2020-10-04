package com.hmittag.gpssilence.objects;

import android.content.Context;
import android.widget.LinearLayout;

public class CustomLinearLayout extends LinearLayout {
    //fields
    private int mId;
    //constructor
    public CustomLinearLayout(Context c, int id)  {
        super(c);
        this.mId = id;
    }
    //methods
    public void setId(int id)   {
        this.mId = id;
    }
    public int getId()  {
        return this.mId;
    }

}
