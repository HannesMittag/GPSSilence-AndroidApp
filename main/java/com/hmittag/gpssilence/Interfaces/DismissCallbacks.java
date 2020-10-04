package com.hmittag.gpssilence.Interfaces;

import android.view.View;

public interface DismissCallbacks {
    /**
     * Called to determine whether the view can be dismissed.
     */
    boolean canDismiss(Object token);

    /**
     * Called when the user has indicated they she would like to dismiss the view.
     *
     * @param view  The originating {@link android.view.View} to be dismissed.
     * @param token The optional token passed to this object's constructor.
     */
    void onDismiss(View view, Object token);
}
