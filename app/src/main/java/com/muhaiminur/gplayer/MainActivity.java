package com.muhaiminur.gplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.second)
    Button second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.second)
    public void onViewClicked() {
        Intent mIntent = Single_Player.getStartIntent(this, VideoPlayerConfig.DEFAULT_VIDEO_URL);
        startActivity(mIntent);
        //startActivity(new Intent(this, Single_Player.class));
    }
}
