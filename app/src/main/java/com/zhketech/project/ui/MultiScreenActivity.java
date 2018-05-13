package com.zhketech.project.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.zhketech.sipclient.R;
import com.zkketech.project.utils.Logutils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

public class MultiScreenActivity extends AppCompatActivity {

    public static final String TAG = MultiScreenActivity.class.getName()+":";
    Context mContext;

    @BindView(R.id.first_surfaceview)
    public SurfaceView surfaceView1;
    private MediaPlayer mMediaPlayer, mMediaPlayer2;
    SurfaceHolder surfaceHolder1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideStatusBar();
        setContentView(R.layout.activity_multi_screen);
        mContext = this;
        ButterKnife.bind(this);
        
        initViewData();
    }

    private void initViewData() {
        boolean isInitVitamio = Vitamio.isInitialized(mContext);
        surfaceHolder1 = surfaceView1.getHolder();
        surfaceHolder1.setFormat(PixelFormat.RGBA_8888);
        initPlayer();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Logutils.i(TAG+"start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logutils.i(TAG+"onResume");


    }
    private void initPlayer() {
        try {
            mMediaPlayer = new MediaPlayer(mContext);
            mMediaPlayer.setDataSource(mContext, Uri.parse("rtmp://live.hkstv.hk.lxdns.com/live/hks"));
            mMediaPlayer.setDisplay(surfaceHolder1);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    Log.i("tag", "infor:" + mp.toString());
                }
            });
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideStatusBar();
        Logutils.i(TAG+"onRestart");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Logutils.i(TAG+"onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Logutils.i(TAG+"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logutils.i(TAG+"onDestroy");

        if (mMediaPlayer != null){
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    protected void hideStatusBar() {
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.INVISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
