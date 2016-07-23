package xyz.msojat.breathingpacer.application;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Marko on 23.7.2016..
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);
    }
}
