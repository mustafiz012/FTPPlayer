package com.freak.musta.ftp.video.player.activities;

import android.os.Bundle;
import android.view.View;

import com.freak.musta.ftp.video.player.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class FullScreenPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_player);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Loading video url
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("url");
        String title = bundle.getString("title");
        JzvdStd jzvdStd = findViewById(R.id.videoplayer);
        jzvdStd.setUp(url, title, Jzvd.SCREEN_WINDOW_NORMAL);
        jzvdStd.thumbImageView.setImageResource(R.drawable.video_placeholder);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
