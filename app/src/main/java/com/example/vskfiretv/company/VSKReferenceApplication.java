package com.example.vskfiretv.company;

import android.app.Application;
import android.util.Log;

import com.example.vskfiretv.company.reporter.DynamicCapabilityReporter;

/**
 * Application class that has global initialization
 */
public class VSKReferenceApplication extends Application {

    private static final String TAG = VSKReferenceApplication.class.getSimpleName();
    private static VSKReferenceApplication application;

    private DynamicCapabilityReporter dynamicCapabilityReporter;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        this.dynamicCapabilityReporter = new DynamicCapabilityReporter(this);

        Log.i(TAG, "Application initialized");
    }

    public static VSKReferenceApplication getInstance() {
        return application;
    }

    public DynamicCapabilityReporter getDynamicCapabilityReporter() {
        return dynamicCapabilityReporter;
    }

}
