package org.denovogroup.rangzen.ui;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Liran on 9/1/2015.
 */
public class RangzenApplication extends Application{

    private static String TAG = "RangzenApplication";
    private static Context appContext;

    @Override
    public final void onCreate() {
        super.onCreate();

        appContext = this;

        /** Initialize Parse */
        Parse.initialize(this, "4XIuXX5JTtAQFQFPJ9M7L1E7o2Tr3oN67bf3hiRU", "02cnF9azewOD0MPqpmfSWpi5TB2XyRTQDY3Rrxno");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static Context getAppContext(){
        return appContext;
    }
}
