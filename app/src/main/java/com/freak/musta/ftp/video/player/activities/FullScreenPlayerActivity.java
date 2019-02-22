package com.freak.musta.ftp.video.player.activities;

import android.os.Bundle;

import com.freak.musta.ftp.video.player.R;

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
