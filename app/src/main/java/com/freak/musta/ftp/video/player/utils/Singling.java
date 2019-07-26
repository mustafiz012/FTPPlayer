package com.freak.musta.ftp.video.player.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.musta.libraries.magic_dialog.CustomDialog;

public class Singling {

    private static final Singling instance = new Singling();
    private CustomDialog mCustomDialog;
    private ProgressDialog mProgressDialog;
    private boolean doubleBackToExitPressedOnce = false;

    private Singling() {
    }

    public static Singling getInstance() {
        return instance;
    }

    public boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            boolean isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            Log.i("Basic Connectivity", "" + activeNetwork.getType());
        }
        return isConnected;
    }

    public void setNoInternetConnectionDialog(Context context) {
        mCustomDialog = new CustomDialog(context);
        mCustomDialog.setTitle("Connectivity Status");
        mCustomDialog.setMessage("No Internet Connection!");
        mCustomDialog.setPositiveButton("Ok", (dialogInterface, i) -> mCustomDialog.dismiss());
        mCustomDialog.show();
    }

    public void setNoActionNeededDialog(Context context, String message) {
        mCustomDialog = new CustomDialog(context);
        mCustomDialog.setTitle("Warning!");
        mCustomDialog.setMessage("" + message);
        mCustomDialog.setPositiveButton("Ok", (dialogInterface, i) -> mCustomDialog.dismiss());
        mCustomDialog.show();
    }

    public void setApplicationExitWarningDialog(Activity activity, Context context) {
        if (doubleBackToExitPressedOnce) {
            activity.moveTaskToBack(true);
        }
        if (!doubleBackToExitPressedOnce) {
            doubleBackToExitPressedOnce = true;
            Toast.makeText(context, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    public void showSimpleMessageDialog(Context context, String title, String message, String positionButtonName) {
        mCustomDialog = new CustomDialog(context);
        if (title != null)
            mCustomDialog.setTitle("" + title);
        mCustomDialog.setMessage("" + message);
        mCustomDialog.setPositiveButton("" + positionButtonName, (dialogInterface, i) -> mCustomDialog.dismiss());
        mCustomDialog.show();
    }

    public boolean isProgressBarShowing() {
        return (mProgressDialog != null && mProgressDialog.isShowing());
    }

    public void startProgressBar(Context context, String title) {
        mProgressDialog = new ProgressDialog(context);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("" + title);
            mProgressDialog.show();
        }
    }

    public void dismissProgressBar() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}