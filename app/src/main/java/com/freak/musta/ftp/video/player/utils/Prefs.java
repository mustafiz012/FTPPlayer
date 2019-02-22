package com.freak.musta.ftp.video.player.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.Set;

public class Prefs {

    public static final String IS_LOGGED_IN = "user_login";
    public static final String IS_FIRST_LAUNCH = "launch_state";
    public static final String CURRENT_USER_PASSWORD = "user_password";
    public static final String NOTIFICATION_DATE = "notification_date";
    public static final String NOTIFICATION_TIME = "notification_time";
    public static final String REMINDER_ME = "REMINDER_ME";
    public static final String SHORT_NOTE = "SHORT_NOTE";
    public static final String YEAR = "YEAR";
    public static final String MONTHS = "MONTHS";
    public static final String DAY = "DAY";
    public static final String HOUR = "HOUR";
    public static final String MINITUE = "MINITUE";
    public static final String USER_ID = "user_id";
    public static final String REGION_LIST = "region_list";
    public static final String QUESTION_LIST = "question_list";
    public static final String PRE_LOAD = "preLoad";
    public static final String FTP_URL = "ftp_url";
    private static final String PREFS_NAME = "bbarv1";
    private static Prefs instance;
    private static SharedPreferences sharedPreferences = null;

    private Prefs(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Prefs getInstance(Context context) {
        if (instance == null) {
            instance = new Prefs(context);
        }
        return instance;
    }

    public void setBooleanValue(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    public boolean getBooleanValue(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void setArrayListValue(String key, Set<String> value) {
        sharedPreferences.edit().putStringSet(key, value).apply();
    }

    public Set getArrayListValue(String key) {
        return sharedPreferences.getStringSet(key, null);
    }

    public void setStringValue(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getStringValue(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public void setIntValue(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getIntValue(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public int getIntValueWithNegativeDefault(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void setLongValue(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLongValue(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    public void setFloatValue(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public float getFloatValue(String key) {
        return sharedPreferences.getFloat(key, 0f);
    }

    public boolean getPreLoad() {
        return sharedPreferences.getBoolean(PRE_LOAD, false);
    }

    public void setPreLoad(boolean totalTime) {
        sharedPreferences
                .edit()
                .putBoolean(PRE_LOAD, totalTime)
                .apply();
    }
}
