package com.freak.musta.ftp.video.player.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.freak.musta.ftp.video.player.R;
import com.freak.musta.ftp.video.player.utils.SingletonInteraction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.musta.libraries.magic_dialog.CustomDialog;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WebView wv_web_viewer;
    private String mUrl = "https://mojaloss.net/directlink/";
    private ArrayList<String> previousUrlList;
    private CustomDialog mCustomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    SingletonInteraction.getInstance().dismissProgressBar(MainActivity.this);
                    //wv_web_viewer.loadUrl("javascript:document.getElementsByClassName('slicknav_menu').style.display = 'none'");
                    wv_web_viewer.setVisibility(View.VISIBLE);
                } else {
                    if (!SingletonInteraction.getInstance().isProgressBarShowing())
                        SingletonInteraction.getInstance().startProgressBar(MainActivity.this, "Loading...");
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wv_web_viewer.loadUrl(mUrl);
            }
        });

        FloatingActionButton fab_exit = findViewById(R.id.fab_exit);
        fab_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomDialog = new CustomDialog(MainActivity.this);
                mCustomDialog.setTitle("Exiting!");
                mCustomDialog.setMessage("Do you wanna exit?");
                mCustomDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        mCustomDialog.dismiss();
                    }
                });
                mCustomDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCustomDialog.dismiss();
                    }
                });
                mCustomDialog.show();
            }
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
        /*int urlSize = previousUrlList.size();
        if (urlSize > 1) {
            mUrl = previousUrlList.get(urlSize - 2).toString();
            wv_web_viewer.setVisibility(View.GONE);
            wv_web_viewer.loadUrl(mUrl);
            previousUrlList.remove(urlSize-1);
            return;
        }
        super.onBackPressed();*/
        SingletonInteraction.getInstance().setApplicationExitWarningDialog(this, getApplicationContext());
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
