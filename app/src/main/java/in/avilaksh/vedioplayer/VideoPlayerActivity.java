package in.avilaksh.vedioplayer;

import android.app.PictureInPictureParams;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Rational;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VideoPlayerActivity extends AppCompatActivity implements Player.EventListener, ExtractorMediaSource.EventListener {

    SimpleExoPlayer player;
    SurfaceView surfaceView;
    public static final int RENDER_COUNT = 1;
    private SeekBar seekPlayerProgress;
    private Handler handler;
    private ImageView btnPlay, btn_lock, btn_unlock;
    private TextView txtCurrentTime, txtEndTime;
    private boolean isPlaying = true;
    private static final String TAG = "VideoPlayerActivity";
    private View decorView;
    private int uiImersiveOptions;
    private RelativeLayout loadingPanel, lockPanel, rootControlView;
    Runnable updatePlayerControl;
    Handler mainHandler;
    //remove final,then modify that:

    private Renderer videoRenderer;
    private AspectRatioFrameLayout frameLayout;
    private String errorString;
    private EventLogger eventLogger;
    private MediaMetadataRetriever retriever;
    private TelephonyManager mTelephonyMgr;

    //and add the set method:
    public void setRendererBuilder(Renderer rendererBuilder) {
        this.videoRenderer = rendererBuilder;
    }

    public void stop() {
        player.stop();
    }

    private Display display;
    private Point size;

    private int sWidth, sHeight;
    private float baseX, baseY;
    private long diffX, diffY;
    private int calculatedTime;
    private String seekDur;
    private Boolean tested_ok = false;
    private Boolean screen_swipe_move = false;
    private boolean immersiveMode, intLeft, intRight, intTop, intBottom, finLeft, finRight, finTop, finBottom;
    private static final int MIN_DISTANCE = 150;
    private ContentResolver cResolver;
    private Window window;
    private LinearLayout volumeBarContainer, brightnessBarContainer, brightness_center_text, vol_center_text, seekBar_center_text, screenLayout;
    private ProgressBar volumeBar, brightnessBar;
    private TextView vol_perc_center_text, brigtness_perc_center_text, txt_seek_secs, txt_seek_currTime, textView_VideoName, screenSizeText;
    private ImageView volIcon, brightnessIcon, vol_image, brightness_image, change_Screen_Size, exo_next_videoButton, exo_back_Videobutton, screenRotationButton;
    private int brightness, mediavolume, device_height, device_width;
    private AudioManager audioManager;
    ImageView bactTOPlaylistButton;
    private PlayerNotificationManager playerNotificationManager;


    private String vedio_path;
    private String vedio_name;
    private int vedio_position;
    private double seekSpeed;
    int screenSize = 0;////0 for Fittoscreen, 1 for Crop,2 for Strecth

    @Override
    public void onLoadError(IOException error) {
        Toast.makeText(this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
        player.release();
        finish();

    }

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        textView_VideoName.setText(videoPlaylist.get(player.getCurrentPeriodIndex()).getmDisplayName());

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {


    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == PlaybackState.STATE_PLAYING) {
            hideAllControls();
            applyAspectRatio(frameLayout, player);
        } else if (playbackState == PlaybackState.STATE_BUFFERING) {
            loadingPanel.setVisibility(View.VISIBLE);
            applyAspectRatio(frameLayout, player);

        } else if (playbackState == Player.STATE_ENDED) {
            player.release();
            finish();
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
        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
            player.release();
            finish();
        } else {
            player.release();
            finish();
        }


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


    public enum ControlModes {

        LOCK, FULLCONTROL
    }

    public enum ScreenSize {
        CROP, FIT_TO_SCREEN, STRETCH,
    }

    ControlModes controlModes;
    Handler screenHandler;
    Runnable runnableScreen = new Runnable() {
        @Override
        public void run() {
            screenLayout.setVisibility(View.GONE);

        }
    };


    {
        updatePlayerControl = new Runnable() {
            @Override
            public void run() {
                hideAllControls();
            }
        };
    }

    ImageView pipModeButton;
    private ArrayList<VedioFileModel> videoPlaylist = new ArrayList<VedioFileModel>();
    int rotation = 1;

//    private AspectRatioFrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_player);
        mainHandler = new Handler();
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        sWidth = size.x;
        sHeight = size.y;


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        device_height = displaymetrics.heightPixels;
        device_width = displaymetrics.widthPixels;


        txtCurrentTime = (TextView) findViewById(R.id.textViewCurrentTime);
        pipModeButton = (ImageView) findViewById(R.id.enterintiPip);
        change_Screen_Size = (ImageView) findViewById(R.id.changeScreenSize);
        lockPanel = (RelativeLayout) findViewById(R.id.lockpanel);
        rootControlView = (RelativeLayout) findViewById(R.id.rootView);
        rootControlView.setVisibility(View.VISIBLE);
        btn_lock = (ImageView) findViewById(R.id.lock_screen);
        btn_unlock = (ImageView) findViewById(R.id.unlock_screen);
        txtEndTime = (TextView) findViewById(R.id.textViewTotalTime);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        frameLayout = (AspectRatioFrameLayout) findViewById(R.id.exo_video_frame);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        seekPlayerProgress = (SeekBar) findViewById(R.id.playerSeekBar);
        volumeBarContainer = (LinearLayout) findViewById(R.id.volume_slider_container);
        brightnessBarContainer = (LinearLayout) findViewById(R.id.brightness_slider_container);
        seekBar_center_text = (LinearLayout) findViewById(R.id.seekbar_center_text);
        textView_VideoName = (TextView) findViewById(R.id.textViewVideoName);
        bactTOPlaylistButton = (ImageView) findViewById(R.id.backtoPlaylistButton);
        screenRotationButton = (ImageView) findViewById(R.id.screenRotation);
        screenRotationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rotation == 0) {
                    rotation = 1;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (rotation == 1) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    rotation = 0;
                }
            }
        });
        bactTOPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                player.release();
            }
        });
//        onlySeekbar = (LinearLayout) findViewById(R.id.seekbar_time);
//        top_controls = (LinearLayout) findViewById(R.id.top);

        vol_perc_center_text = (TextView) findViewById(R.id.vol_perc_center_text);
        brigtness_perc_center_text = (TextView) findViewById(R.id.brigtness_perc_center_text);
        volumeBar = (ProgressBar) findViewById(R.id.volume_slider);
        brightnessBar = (ProgressBar) findViewById(R.id.brightness_slider);
        volumeBarContainer = (LinearLayout) findViewById(R.id.volume_slider_container);
        brightnessBarContainer = (LinearLayout) findViewById(R.id.brightness_slider_container);
        brightness_center_text = (LinearLayout) findViewById(R.id.brightness_center_text);
        vol_center_text = (LinearLayout) findViewById(R.id.vol_center_text);

        volIcon = (ImageView) findViewById(R.id.volIcon);
        brightnessIcon = (ImageView) findViewById(R.id.brightnessIcon);
        vol_image = (ImageView) findViewById(R.id.vol_image);
        brightness_image = (ImageView) findViewById(R.id.brightness_image);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        txt_seek_secs = (TextView) findViewById(R.id.txt_seek_secs);
        txt_seek_currTime = (TextView) findViewById(R.id.txt_seek_currTime);
        exo_back_Videobutton = (ImageView) findViewById(R.id.exo_prev_Button);
        exo_next_videoButton = (ImageView) findViewById(R.id.exo_next_Button);
        screenLayout = (LinearLayout) findViewById(R.id.screenLayout);
        screenLayout.setVisibility(View.GONE);
        screenSizeText = (TextView) findViewById(R.id.texviewScreenSize);

        uiImersiveOptions = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiImersiveOptions);
        //exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_video_frame);
        //player = (ExoPlayer) findViewById(R.id.exo_video_frame);
        if (getIntent().getExtras() != null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            vedio_path = bundle.getString("SelectVideoFilePath");
            vedio_name = bundle.getString("SelectVideoName");
            vedio_position = bundle.getInt("SelectVideoposition");
            videoPlaylist = bundle.getParcelableArrayList("VideoPlaylist");


//            Uri video = Uri.parse(vedio_path.toString().split(",>")[0]);


            try {
                retriever = new MediaMetadataRetriever();
                retriever.setDataSource("" + vedio_path.split(",>")[0]);
                int ori = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                retriever.release();
                if (ori == 90) {
                    rotation = 0;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    rotation = 1;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }


            } catch (NumberFormatException e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();

            }
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

// Request audio focus for playback
            int result = am.requestAudioFocus(focusChangeListener,
// Use the music stream.
                    AudioManager.STREAM_MUSIC,
// Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);


            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                execute(vedio_position);
// other app had stopped playing song now , so u can do u stuff now .
            }
//            execute(vedio_position);


//            p
//repareExoPlayerFromFileUri(video, vedio_name);


            //do here
        } else {
            Toast.makeText(VideoPlayerActivity.this, "Video not found", Toast.LENGTH_LONG).show();
        }


        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlModes = ControlModes.FULLCONTROL;
                rootControlView.setVisibility(View.VISIBLE);
                lockPanel.setVisibility(View.GONE);


            }
        });
        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlModes = ControlModes.LOCK;
                rootControlView.setVisibility(View.GONE);
                hideAllControls();
                lockPanel.setVisibility(View.VISIBLE);
            }
        });
        screenHandler = new Handler();

        change_Screen_Size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenSize == 0) {
                    changeAspectRatio(1);
                    screenSize = 1;
                    screenSizeText.setText("FILL");
                    screenLayout.setVisibility(View.VISIBLE);
                    screenHandler.postDelayed(runnableScreen, 1500);


                } else if (screenSize == 1) {
                    changeAspectRatio(2);
                    screenSize = 2;
                    screenSizeText.setText("CROP");
                    screenLayout.setVisibility(View.VISIBLE);
                    screenHandler.postDelayed(runnableScreen, 1500);
                } else if (screenSize == 2) {
                    changeAspectRatio(0);
                    screenSize = 0;
                    screenSizeText.setText("FIT To SCREEN");
                    screenLayout.setVisibility(View.VISIBLE);
                    screenHandler.postDelayed(runnableScreen, 1500);
                }


            }
        });

        pipModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    hideAllControls();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Rational rational = new Rational(frameLayout.getWidth(),
                                frameLayout.getHeight());

                        PictureInPictureParams mParams =
                                new PictureInPictureParams.Builder()
                                        .setAspectRatio(rational)
                                        .build();

                        enterPictureInPictureMode(mParams);
                    } else {
                        Toast.makeText(VideoPlayerActivity.this, "Not Supported", Toast.LENGTH_SHORT).show();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }


        });
        exo_next_videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.next();
            }
        });
        exo_back_Videobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.previous();
            }
        });

        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyMgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // Test for incoming call, dialing call, active or on hold
            if (state == TelephonyManager.CALL_STATE_RINGING || state == TelephonyManager.CALL_STATE_OFFHOOK) {
                player.stop();  // Put here the code to stop your music
            }

            super.onCallStateChanged(state, incomingNumber);
        }
    };

    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    switch (focusChange) {

                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            // Lower the volume while ducking.
                            player.setVolume(0.2f);
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                            player.stop();
                            break;

                        case (AudioManager.AUDIOFOCUS_LOSS):
                            stop();
                            ComponentName component = new ComponentName(VideoPlayerActivity.this, MediaMetadataRetriever.class);
                            am.unregisterMediaButtonEventReceiver(component);
                            break;

                        case (AudioManager.AUDIOFOCUS_GAIN):
                            // Return the volume to normal and resume if paused.
                            player.setVolume(1f);
                            player.setPlayWhenReady(true);
                            break;
                        default:
                            break;
                    }
                }
            };


    @Override
    protected void onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            enterPictureInPictureMode();
        } else {
            Toast.makeText(this, "Device not Supported", Toast.LENGTH_SHORT).show();
        }
        super.onUserLeaveHint();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && isInPictureInPictureMode()) {

            player.getPlaybackState();
            // Continue playback...


        } else {
            player.release();
            Toast.makeText(VideoPlayerActivity.this, "ON Pause", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && isInPictureInPictureMode()) {
            // Continue playback...
            player.getPlaybackState();

        } else {
            player.release();
            Toast.makeText(VideoPlayerActivity.this, "ON Pause", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (!isInPictureInPictureMode) {
            pipModeButton.setVisibility(View.VISIBLE);
            showAllControls();
        } else {
            pipModeButton.setVisibility(View.GONE);
            hideAllControls();
        }
    }

    public void changeAspectRatio(int screenSiz) {
        if (screenSiz == 0) {
            frameLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        } else if (screenSiz == 1) {
            frameLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

        } else if (screenSiz == 2) {
            frameLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        }
    }


    private void applyAspectRatio(AspectRatioFrameLayout aspectRatioFrameLayout, SimpleExoPlayer exoPlayer) {
        screenSize = 0;

        float videoRatio = (float) exoPlayer.getVideoFormat().width / exoPlayer.getVideoFormat().height;
        aspectRatioFrameLayout.setAspectRatio(videoRatio);

//        }
    }

    private void execute(int position) {

//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        TrackSelector trackSelector = new DefaultTrackSelector(new DefaultBandwidthMeter());
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        if (player != null) {


            String useragent = Util.getUserAgent(this, "ua");
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, useragent);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //  MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, mainHandler, VideoPlayerActivity.this);

            MediaSource[] mediaSources = new MediaSource[videoPlaylist.size()];
            for (int i = 0; i < mediaSources.length; i++) {
                mediaSources[i] = new ExtractorMediaSource(Uri.parse(videoPlaylist.get(i).getmUrl_FilePath()), dataSourceFactory, extractorsFactory, mainHandler, VideoPlayerActivity.this);


            }
// Plays the first video, then the second video.
            MediaSource m = mediaSources.length == 1 ? mediaSources[0]
                    : new ConcatenatingMediaSource(mediaSources);


            player.setVideoSurfaceView(surfaceView);
            player.addListener(this);
            player.prepare(m);
            textView_VideoName.setText(videoPlaylist.get(position).getmDisplayName());
            player.seekTo(position, C.TIME_UNSET);
            player.setPlayWhenReady(true);
            initMediaControls();
            loadingPanel.setVisibility(View.GONE);
            mainHandler.postDelayed(updatePlayerControl, 3000);

            controlModes = ControlModes.FULLCONTROL;


            // exoPlayerView.setPlayer(player);

        }


    }


    public Handler getMainHandler() {
        return mainHandler;
    }


    public Runnable getUpdatePlayerControl() {
        return updatePlayerControl;
    }

    private void hideAllControls() {
        if (controlModes == ControlModes.FULLCONTROL) {
            if (rootControlView.getVisibility() == View.VISIBLE) {
                rootControlView.setVisibility(View.GONE);


            }


        } else if (controlModes == ControlModes.LOCK) {
            if (lockPanel.getVisibility() == View.VISIBLE) {
                lockPanel.setVisibility(View.GONE);

            }


        }
        decorView.setSystemUiVisibility(uiImersiveOptions);


    }

    private void showAllControls() {
        if (controlModes == ControlModes.FULLCONTROL) {
            if (rootControlView.getVisibility() == View.GONE) {
                rootControlView.setVisibility(View.VISIBLE);


            }


        } else if (controlModes == ControlModes.LOCK) {
            if (lockPanel.getVisibility() == View.GONE) {
                lockPanel.setVisibility(View.VISIBLE);

            }


        }
        mainHandler.removeCallbacks(updatePlayerControl);
        mainHandler.postDelayed(updatePlayerControl, 7000);


    }

    private boolean hasWriteSettingsPermission(Context context) {
        boolean ret = true;
        // Get the result from below code.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ret = Settings.System.canWrite(context);
        }
        return ret;
    }

    // Start can modify system settings panel to let user change the write settings permission.
    private void changeWriteSettingsPermission(Context context) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void changeScreenBrightness(Context context, int screenBrightnessValue) {
        // Change the screen brightness change mode to manual.
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);

        /*
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = screenBrightnessValue / 255f;
        window.setAttributes(layoutParams);
        */
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tested_ok = false;
                if (event.getX() < (sWidth / 2)) {
                    intLeft = true;
                    intRight = false;
                } else if (event.getX() > (sWidth / 2)) {
                    intLeft = false;
                    intRight = true;
                }
                int upperLimit = (sHeight / 4) + 100;
                int lowerLimit = ((sHeight / 4) * 3) - 150;
                if (event.getY() < upperLimit) {
                    intBottom = false;
                    intTop = true;
                } else if (event.getY() > lowerLimit) {
                    intBottom = true;
                    intTop = false;
                } else {
                    intBottom = false;
                    intTop = false;
                }
                seekSpeed = (TimeUnit.MILLISECONDS.toSeconds(player.getDuration()) * 0.1);
                diffX = 0;
                calculatedTime = 0;
                seekDur = stringForTime((int) diffX);
//                seekDur = String.format("%02d:%02d",
//                        TimeUnit.MILLISECONDS.toMinutes(diffX) -
//                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diffX)),
//                        TimeUnit.MILLISECONDS.toSeconds(diffX) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diffX)));

                //TOUCH STARTED
                baseX = event.getX();
                baseY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                screen_swipe_move = true;
                if (controlModes == ControlModes.FULLCONTROL) {
                    rootControlView.setVisibility(View.GONE);
                    diffX = (long) (Math.ceil(event.getX() - baseX));
                    diffY = (long) Math.ceil(event.getY() - baseY);
                    double brightnessSpeed = 0.10;
                    if (Math.abs(diffY) > MIN_DISTANCE) {
                        tested_ok = true;
                    }
                    if (Math.abs(diffY) > Math.abs(diffX)) {
                        if (intLeft) {
                            Context context = getApplicationContext();

                            // Check whether has the write settings permission or not.
                            boolean settingsCanWrite = hasWriteSettingsPermission(context);

                            // If do not have then open the Can modify system settings panel.
                            if (!settingsCanWrite) {
                                changeWriteSettingsPermission(context);
                            } else {

                                cResolver = getContentResolver();
                                window = getWindow();
                                try {
                                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                    brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
                                } catch (Settings.SettingNotFoundException e) {
                                    e.printStackTrace();
                                }
                                int new_brightness = (int) (brightness - (diffY * brightnessSpeed));
                                if (new_brightness > 250) {
                                    new_brightness = 250;
                                } else if (new_brightness < 1) {
                                    new_brightness = 1;
                                }
                                double brightPerc = Math.ceil((((double) new_brightness / (double) 250) * (double) 100));
                                brightnessBarContainer.setVisibility(View.VISIBLE);
                                brightness_center_text.setVisibility(View.VISIBLE);
                                brightnessBar.setProgress((int) brightPerc);
                                if (brightPerc < 30) {
                                    brightnessIcon.setImageResource(R.drawable.brightnessminimum);
                                    brightness_image.setImageResource(R.drawable.brightnessminimum);
                                } else if (brightPerc > 30 && brightPerc < 80) {
                                    brightnessIcon.setImageResource(R.drawable.brightness_medium);
                                    brightness_image.setImageResource(R.drawable.brightness_medium);
                                } else if (brightPerc > 80) {
                                    brightnessIcon.setImageResource(R.drawable.brightness_maximum);
                                    brightness_image.setImageResource(R.drawable.brightness_maximum);
                                }
                                brigtness_perc_center_text.setText(" " + (int) brightPerc);
                                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, (new_brightness));
                                WindowManager.LayoutParams layoutpars = window.getAttributes();
                                layoutpars.screenBrightness = brightness / (float) 255;
                                window.setAttributes(layoutpars);
                            }
                        } else if (intRight) {
                            vol_center_text.setVisibility(View.VISIBLE);
                            mediavolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            double cal = (double) diffY * ((double) maxVol / (double) (device_height * 1.5));
                            int newMediaVolume = mediavolume - (int) cal;
                            if (newMediaVolume > maxVol) {
                                newMediaVolume = maxVol;
                            } else if (newMediaVolume < 1) {
                                newMediaVolume = 0;
                            }
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newMediaVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                            double volPerc = Math.ceil((((double) newMediaVolume / (double) maxVol) * (double) 100));
                            vol_perc_center_text.setText(" " + (int) volPerc);
                            if (volPerc < 1) {
                                volIcon.setImageResource(R.drawable.volume_mute);
                                vol_image.setImageResource(R.drawable.volume_mute);
                                vol_perc_center_text.setVisibility(View.GONE);
                            } else if (volPerc >= 1) {
                                volIcon.setImageResource(R.drawable.volume_icon);
                                vol_image.setImageResource(R.drawable.volume_icon);
                                vol_perc_center_text.setVisibility(View.VISIBLE);
                            }
                            volumeBarContainer.setVisibility(View.VISIBLE);
                            volumeBar.setProgress((int) volPerc);
                        }
                    } else if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > (MIN_DISTANCE + 100)) {
                            tested_ok = true;
                            //  rootControlView.setVisibility(View.VISIBLE);
                            seekBar_center_text.setVisibility(View.VISIBLE);
                            String totime = "";
                            calculatedTime = (int) ((diffX) * seekSpeed);
                            if (calculatedTime > 0) {
                                seekDur = stringForTime(calculatedTime);
//                                seekDur = String.format("[ +%02d:%02d ]",
//                                        TimeUnit.MILLISECONDS.toMinutes(calculatedTime) -
//                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(calculatedTime)),
//                                        TimeUnit.MILLISECONDS.toSeconds(calculatedTime) -
//                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(calculatedTime)));
                            } else if (calculatedTime < 0) {
                                seekDur = stringForTime(calculatedTime);
//                                seekDur = String.format("[ -%02d:%02d ]",
//                                        Math.abs(TimeUnit.MILLISECONDS.toMinutes(calculatedTime) -
//                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(calculatedTime))),
//                                        Math.abs(TimeUnit.MILLISECONDS.toSeconds(calculatedTime) -
//                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(calculatedTime))));
                            }
                            //seekDur = stringForTime(calculatedTime);
                            totime = stringForTime((int) (player.getCurrentPosition() + (calculatedTime)));
//                            totime = String.format("%02d:%02d",
//                                    TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition() + (calculatedTime)) -
//                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition() + (calculatedTime))), // The change is in this line
//                                    TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition() + (calculatedTime)) -
//                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition() + (calculatedTime))));
                            txt_seek_secs.setText("[ +" + seekDur + " ]");
                            txt_seek_currTime.setText(totime);
                            seekPlayerProgress.setProgress((int) (player.getCurrentPosition() + (calculatedTime)));
                            hideAllControls();
                            initMediaControls();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                screen_swipe_move = false;
                tested_ok = false;

                seekBar_center_text.setVisibility(View.GONE);
                brightness_center_text.setVisibility(View.GONE);
                vol_center_text.setVisibility(View.GONE);
                brightnessBarContainer.setVisibility(View.GONE);
                volumeBarContainer.setVisibility(View.GONE);


                // rootControlView.setVisibility(View.VISIBLE);
                calculatedTime = (int) (player.getCurrentPosition() + (calculatedTime));
                player.seekTo(calculatedTime);
                showAllControls();
                break;

        }
        return super.onTouchEvent(event);

    }


    @Override
    public void onBackPressed() {
        player.release();
        super.onBackPressed();
    }


    private void initMediaControls() {
        initPlayButton();
        initSeekBar();
        setProgress();

    }

    private void initPlayButton() {
        btnPlay = (ImageView) findViewById(R.id.exo_play_Button);
        btnPlay.requestFocus();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayPause(!isPlaying);
            }
        });
    }

    //
//    /**
//     * Starts or stops playback. Also takes care of the Play/Pause button toggling
//     *
//     * @param play True if playback should be started
//     */
    private void setPlayPause(boolean play) {
        isPlaying = play;
        player.setPlayWhenReady(play);
        if (!isPlaying) {
            //  setProgress();
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
        } else {
            setProgress();
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        }
    }


    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {
        seekPlayerProgress.setMax((int) player.getDuration() / 1000);
        txtCurrentTime.setText(stringForTime((int) player.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int) player.getDuration()));

        if (handler == null) handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (player != null && isPlaying) {
                    seekPlayerProgress.setMax((int) player.getDuration() / 1000);
                    int mCurrentPosition = (int) player.getCurrentPosition() / 1000;
                    seekPlayerProgress.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int) player.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int) player.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }


    private void initSeekBar() {
        seekPlayerProgress = (SeekBar) findViewById(R.id.playerSeekBar);
        seekPlayerProgress.requestFocus();

        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                player.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) player.getDuration() / 1000);

    }


}

