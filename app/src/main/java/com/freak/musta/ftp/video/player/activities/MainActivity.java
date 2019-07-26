package com.freak.musta.ftp.video.player.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.freak.musta.ftp.video.player.R;
import com.freak.musta.ftp.video.player.utils.AppConstants;
import com.freak.musta.ftp.video.player.utils.Prefs;
import com.freak.musta.ftp.video.player.utils.Singling;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.musta.libraries.magic_dialog.CustomDialog;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WebView wv_web_viewer;
    private String mUrl = "https://mojaloss.net/directlink/";
    private ArrayList<String> previousUrlList;
    private CustomDialog mCustomDialog;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUrl = Prefs.getInstance(mContext).getStringValue(Prefs.FTP_URL, mUrl);

        previousUrlList = new ArrayList<>();
        wv_web_viewer = findViewById(R.id.wv_web_viewer);
        wv_web_viewer.getSettings().setJavaScriptEnabled(true);
        wv_web_viewer.canGoBack();
        wv_web_viewer.canGoForward();
        addToPreviousUrlList(mUrl);

        if (mUrl != null) {
            wv_web_viewer.setVisibility(View.GONE);
            wv_web_viewer.loadUrl(mUrl);
            wv_web_viewer.loadUrl("javascript:document.getElementsByClassName('slicknav_menu').style.display = 'none';");
        } else {
            Toast.makeText(MainActivity.this, "No valid link found!!", Toast.LENGTH_SHORT).show();
        }

        wv_web_viewer.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int loadingProgress) {
                if (loadingProgress >= 60) {
                    Singling.getInstance().dismissProgressBar();
                    //wv_web_viewer.loadUrl("javascript:document.getElementsByClassName('slicknav_menu').style.display = 'none'");
                    wv_web_viewer.setVisibility(View.VISIBLE);
                } else {
                    if (!Singling.getInstance().isProgressBarShowing())
                        Singling.getInstance().startProgressBar(MainActivity.this, "Loading...");
                    try {
                        Log.i(TAG, "CurrentUrl: " + view.getUrl());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        wv_web_viewer.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                String fileName = Uri.parse(url).getLastPathSegment();
                if (fileName != null && (fileName.endsWith(".mp4") || fileName.endsWith(".mkv"))) {
                    Log.i(TAG, "File to load: " + fileName);
                } else {
                    addToPreviousUrlList(url);
                    super.onLoadResource(view, url);
                }
            }

            /*@Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Ion.with(getApplicationContext()).load(url).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.i(TAG, "onCompleted html: " + result);
                    }
                });

                String fileName = Uri.parse(url).getLastPathSegment();
                if (fileName != null && (fileName.contains(".mp4") || fileName.contains(".mkv"))) {
                    Log.i(TAG, "shouldOverrideUrlLoading: " + fileName);
                    return true;
                }

                addToPreviousUrlList(url);
                mUrl = url;
                return super.shouldOverrideUrlLoading(view, url);
            }*/

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        wv_web_viewer.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            String fileName = Uri.parse(url).getLastPathSegment();
            if (fileName != null && (fileName.contains(".mp4") || fileName.contains(".mkv"))) {
                Log.i(TAG, "onDownloadStart: " + fileName);
                startPlayer(url);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_reload_url);
        fab.setOnClickListener(view -> wv_web_viewer.loadUrl(mUrl));

        FloatingActionButton fab_exit = findViewById(R.id.fab_exit);
        fab_exit.setOnClickListener(view -> {
            mCustomDialog = new CustomDialog(MainActivity.this);
            mCustomDialog.setTitle("Exiting!");
            mCustomDialog.setMessage("Do you wanna exit?");
            mCustomDialog.setPositiveButton("Ok", (dialogInterface, i) -> {
                finish();
                mCustomDialog.dismiss();
            });
            mCustomDialog.setNegativeButton("Cancel", (dialog, which) -> mCustomDialog.dismiss());
            mCustomDialog.show();
        });
    }

    private void startPlayer(String url) {
        Intent intent = new Intent(this, FullScreenPlayerActivity.class);
        Bundle bundle = new Bundle();
        String fileName = Uri.parse(url).getLastPathSegment();
        bundle.putString("url", url);
        if (fileName.endsWith(".mp4")) {
            bundle.putString("title", fileName.split(".mp4")[0]);
        } else if (fileName.endsWith(".mkv")) {
            bundle.putString("title", fileName.split(".mkv")[0]);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void addToPreviousUrlList(String url) {
        if (previousUrlList.size() == 0) {
            previousUrlList.add("" + url);
        } else {
            for (int i = 0; i < previousUrlList.size(); i++) {
                if (url.equals(previousUrlList.get(i))) {
                    previousUrlList.remove(i);
                    previousUrlList.add(url);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Singling.getInstance().setApplicationExitWarningDialog(this, getApplicationContext());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && wv_web_viewer.canGoBack()) {
            wv_web_viewer.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_url) {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.change_url_view);

            int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * AppConstants.POPUP_DIALOG_WIDTH);
            int height = (mContext.getResources().getDisplayMetrics().heightPixels);

            dialog.getWindow().setLayout(width, height);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);

            EditText et_change_url = dialog.findViewById(R.id.et_change_url);
            et_change_url.setText(mUrl);
            TextView positive_button = dialog.findViewById(R.id.positive_button);
            TextView negative_button = dialog.findViewById(R.id.negative_button);

            negative_button.setOnClickListener(v -> dialog.dismiss());
            positive_button.setOnClickListener(v -> {
                String input = et_change_url.getText().toString();
                if (input.startsWith("http://")) {
                    mUrl = input.replace("http://", "https://");
                } else if (!input.startsWith("https://")) {
                    mUrl = "https://" + input;
                } else {
                    mUrl = input;
                }
                Prefs.getInstance(mContext).setStringValue(Prefs.FTP_URL, mUrl);
                wv_web_viewer.loadUrl(mUrl);
                dialog.dismiss();
            });

            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
