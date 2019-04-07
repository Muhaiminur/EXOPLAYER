package com.muhaiminur.gplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Third_Activity extends AppCompatActivity implements PlaybackPreparer, PlayerControlView.VisibilityListener, Player.EventListener, AdapterView.OnItemSelectedListener {

    @BindView(R.id.videoFullScreenPlayer)
    PlayerView videoFullScreenPlayer;
    @BindView(R.id.spinnerVideoDetails)
    ProgressBar spinnerVideoDetails;
    @BindView(R.id.video_quality)
    Spinner videoQuality;


    VIDEO_QUALITY quality;
    //
    private static final String TAG = "ExoPlayerActivity";
    private static final String KEY_VIDEO_URI = "video_uri";
    String videoUri;
    SimpleExoPlayer player;
    Handler mHandler;
    Runnable mRunnable;
    DefaultTrackSelector trackSelector;

    public static Intent getStartIntent(Context context, String videoUri) {
        Intent intent = new Intent(context, Third_Activity.class);
        intent.putExtra(KEY_VIDEO_URI, videoUri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_third_);
        ButterKnife.bind(this);
        init_object();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getSupportActionBar().hide();
        if (getIntent().hasExtra(KEY_VIDEO_URI)) {
            videoUri = getIntent().getStringExtra(KEY_VIDEO_URI);
        }
        setUp();
        videoFullScreenPlayer.setControllerVisibilityListener(this);
        videoFullScreenPlayer.requestFocus();
        videoFullScreenPlayer.setShutterBackgroundColor(Color.TRANSPARENT);
        Log.d("Third Activity", "Created");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.quality_array, R.layout.spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        videoQuality.setAdapter(adapter);
        videoQuality.setOnItemSelectedListener(this);
        videoQuality.setSelection(2);
    }

    /*@OnClick(R.id.video_quality)
    public void onViewClicked() {
        long time = player.getCurrentPosition();
        buildMediaSource(Uri.parse("http://vdog.radiogbd.com/video/720p/176.mp4"));
        player.seekTo(0, time);
    }*/

    private void setUp() {
        initializePlayer();
        if (videoUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(videoUri));
    }

    private void initializePlayer() {
        if (player == null) {
            //Create a default Loadcontrol
            /*LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    2 * VideoPlayerConfig.MIN_BUFFER_DURATION,
                    2 * VideoPlayerConfig.MAX_BUFFER_DURATION,
                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);*/
            LoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(2 * VideoPlayerConfig.MIN_BUFFER_DURATION, 2 * VideoPlayerConfig.MAX_BUFFER_DURATION, VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER, VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER).createDefaultLoadControl();
            //Create a default TrackSelector
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this), trackSelector, loadControl);
            videoFullScreenPlayer.setPlayer(player);
        }
    }

    private void buildMediaSource(Uri mUri) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        MediaSource videoSource2 = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse("https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4"));
        ConcatenatingMediaSource concatenatedSource =
                new ConcatenatingMediaSource(videoSource, videoSource2);
        // Prepare the player with the source.
        player.prepare(concatenatedSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    private void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
            if (videoFullScreenPlayer != null) {
                videoFullScreenPlayer.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            if (videoFullScreenPlayer != null) {
                videoFullScreenPlayer.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            pausePlayer();
            if (mRunnable != null) {
                mHandler.removeCallbacks(mRunnable);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

            case Player.STATE_BUFFERING:
                spinnerVideoDetails.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                // Activate the force enable
                break;
            case Player.STATE_IDLE:

                break;
            case Player.STATE_READY:
                spinnerVideoDetails.setVisibility(View.GONE);

                break;
            default:
                // status = PlaybackStatus.IDLE;
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onVisibilityChange(int visibility) {
        Log.d("visiblity", "check = " + visibility);
        videoQuality.setVisibility(visibility);


    }

    @Override
    public void preparePlayback() {
        initializePlayer();
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.d("Video Quality", pos + "");
        long time = player.getCurrentPosition();
        switch (pos) {
            case 0:
                buildMediaSource(Uri.parse(quality.getZero()));
                player.seekTo(0, time);
                break;
            case 1:
                buildMediaSource(Uri.parse(quality.getOne()));
                player.seekTo(0, time);
                break;
            case 2:
                buildMediaSource(Uri.parse(quality.getTwo()));
                player.seekTo(0, time);
                break;
            case 3:
                buildMediaSource(Uri.parse(quality.getThree()));
                player.seekTo(0, time);
                break;
            case 4:
                buildMediaSource(Uri.parse(quality.getFour()));
                player.seekTo(0, time);
                break;
            case 5:
                buildMediaSource(Uri.parse(quality.getFive()));
                player.seekTo(0, time);
                break;
            default:
                System.out.println("Invalid grade");
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }

    public void init_object() {
        quality = new VIDEO_QUALITY("http://vdog.radiogbd.com/video/144p/176.mp4", "http://vdog.radiogbd.com/video/240p/176.mp4", "http://vdog.radiogbd.com/video/360p/176.mp4", "http://vdog.radiogbd.com/video/480p/176.mp4", "http://vdog.radiogbd.com/video/720p/176.mp4", "http://vdog.radiogbd.com/video/1080p/176.mp4");
    }
}
