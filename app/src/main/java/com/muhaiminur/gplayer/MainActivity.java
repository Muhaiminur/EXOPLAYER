package com.muhaiminur.gplayer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.second)
    Button second;
    PermissionManager permissionManager;
    @BindView(R.id.third)
    Button third;
    @BindView(R.id.four)
    Button four;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);
        getVideoList();
    }

    private void getVideoList() {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        ArrayList<String> pathArrList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                pathArrList.add(cursor.getString(0));
            }
            cursor.close();
        }
        Log.d("all path", pathArrList.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
    }

    @OnClick({R.id.second, R.id.third})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.second:
                Intent mIntent = Single_Player.getStartIntent(this, VideoPlayerConfig.DEFAULT_VIDEO_URL);
                startActivity(mIntent);
                //startActivity(new Intent(this, Single_Player.class));
                break;
            case R.id.third:
                Intent second = Second_Activity.getStartIntent(this, VideoPlayerConfig.DEFAULT_VIDEO_URL);
                startActivity(second);
                //startActivity(new Intent(this, Second_Activity.class));
                break;
        }
    }

    @OnClick(R.id.four)
    public void onViewClicked() {
        Intent mIntent = Third_Activity.getStartIntent(this, VideoPlayerConfig.DEFAULT_VIDEO_URL);
        startActivity(mIntent);
    }
}
