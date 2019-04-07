# EXOPLAYER
Welcome to the EXOPLAYER wiki!
# Using Exoplayer Play Video
### Add ExoPlayer module dependencies
`implementation 'com.google.android.exoplayer:exoplayer:2.9.6'`
***
### Turn on Java 8 support
`compileOptions {
  targetCompatibility JavaVersion.VERSION_1_8
}`
***
### Define Playerview in Layout
    <com.google.android.exoplayer2.ui.PlayerView
        android:id="@+id/player_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
***
### Initialize Player
    private void initializePlayer() {
        if (player == null) {
            //Create a default Loadcontrol
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

***
### Create Buildsource
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

***
### For Adding Other View like button/spinner to the 
    videoFullScreenPlayer.setControllerVisibilityListener(this);
    videoFullScreenPlayer.requestFocus();
    videoFullScreenPlayer.setShutterBackgroundColor(Color.TRANSPARENT);
**For attaching view with player controller**

    @Override
    public void onVisibilityChange(int visibility) {
        Log.d("visiblity", "check = " + visibility);
        videoQuality.setVisibility(visibility);
    }

***
### Adding progressbar to the player
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

***
### For changing Video
    long time = player.getCurrentPosition();
    buildMediaSource(Uri.parse("url"));//method already defines above
    player.seekTo(0, time);
***
# Customize UI
### Create a layout file like name `exo_playback_custom_control_view.xml`
    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom"
    android:background="#CC000000"
    android:layoutDirection="ltr"
    android:orientation="vertical">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:orientation="horizontal"
        android:paddingTop="4dp">

        <ImageButton
            android:id="@id/exo_prev"
            style="@style/CustomExoMediaButton.Prev"
            android:layout_marginStart="5dp"
            android:layout_marginEnd="5dp" />

        <ImageButton
            android:id="@id/exo_rew"
            style="@style/CustomExoMediaButton.Rewind"
            android:layout_marginStart="5dp"
            android:layout_marginEnd="5dp" />

        <ImageButton
            android:id="@id/exo_play"
            style="@style/CustomExoMediaButton.Play"
            android:layout_marginStart="5dp"
            android:layout_marginEnd="5dp" />

        <ImageButton
            android:id="@id/exo_pause"
            style="@style/CustomExoMediaButton.Pause"
            android:layout_marginStart="5dp"
            android:layout_marginEnd="5dp" />

        <ImageButton
            android:id="@id/exo_ffwd"
            style="@style/CustomExoMediaButton.FastForward"
            android:layout_marginStart="5dp"
            android:layout_marginEnd="5dp" />

        <ImageButton
            android:id="@id/exo_next"
            style="@style/CustomExoMediaButton.Next"
            android:layout_marginStart="5dp"
            android:layout_marginEnd="5dp" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"
        android:gravity="center_vertical"
        android:orientation="horizontal">

        <TextView
            android:id="@id/exo_position"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:includeFontPadding="false"
            android:paddingLeft="4dp"
            android:paddingRight="4dp"
            android:textColor="#FFBEBEBE"
            android:textSize="14sp"
            android:textStyle="bold" />

        <com.google.android.exoplayer2.ui.DefaultTimeBar
            android:id="@id/exo_progress"
            android:layout_width="0dp"
            android:layout_height="26dp"
            android:layout_weight="1" />

        <TextView
            android:id="@id/exo_duration"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:includeFontPadding="false"
            android:paddingLeft="4dp"
            android:paddingRight="4dp"
            android:textColor="#FFBEBEBE"
            android:textSize="14sp"
            android:textStyle="bold" />

    </LinearLayout>
    </LinearLayout>
### Add this to `style.xml` file
    <style name="CustomExoMediaButton">
        <item name="android:background">?android:attr/selectableItemBackground</item>
        <item name="android:scaleType">fitXY</item>
        <item name="android:layout_width">30dp</item>
        <item name="android:layout_height">30dp</item>
    </style>

    <style name="CustomExoMediaButton.Play">
        <item name="android:src">@drawable/play</item>
    </style>

    <style name="CustomExoMediaButton.Pause">
        <item name="android:src">@drawable/pause</item>
    </style>

    <style name="CustomExoMediaButton.Rewind">
        <item name="android:src">@drawable/backward</item>
    </style>

    <style name="CustomExoMediaButton.FastForward">
        <item name="android:src">@drawable/fast_forward</item>
    </style>

    <style name="CustomExoMediaButton.Prev">
        <item name="android:src">@drawable/previous</item>
    </style>

    <style name="CustomExoMediaButton.Next">
        <item name="android:src">@drawable/skip</item>
    </style>

# FOR EXAMPLE
* Video Play From Url

![Video Play From Url](https://github.com/Muhaiminur/EXOPLAYER/blob/master/DOCUMENTATION/PLAY%20VIDEO%20FROM%20URL.gif)

* Customize UI

![Customize Ui](https://github.com/Muhaiminur/EXOPLAYER/blob/master/DOCUMENTATION/CUSTOM%20UI.gif)

* Change Video With LINK

![Change Video With LINK](https://github.com/Muhaiminur/EXOPLAYER/blob/master/DOCUMENTATION/DIFFERENT%20QUALITY.gif)

You Can Download Apk From [Here](https://github.com/Muhaiminur/EXOPLAYER/blob/master/DOCUMENTATION/APK/app-debug.apk)
