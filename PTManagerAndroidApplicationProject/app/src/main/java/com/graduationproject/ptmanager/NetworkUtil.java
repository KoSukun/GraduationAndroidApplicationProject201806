package com.graduationproject.ptmanager;

import android.annotation.SuppressLint;
import android.os.StrictMode;

/**
 * Created by sukun on 2018-03-28.
 */

public class NetworkUtil {
    @SuppressLint("NewApi")
    static public void setNetworkPolicy() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}