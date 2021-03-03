package com.pierfrancescosoffritti.androidyoutubeplayer.core.sampleapp.examples;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.EmbedConfig;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.aytplayersample.R;

public class SCMPExampleActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private FrameLayout playerContainer;
    private EditText iuEditText;
    private EditText videoIdText;
    private EditText originEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scmp_example);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        playerContainer = findViewById(R.id.player_container);
        iuEditText = findViewById(R.id.iu_editText);
        videoIdText = findViewById(R.id.videoIdEditText);
        originEditText = findViewById(R.id.origin_EditText);

        setButtonClickListener();
    }

    private void initYouTubePlayerView(boolean isSmartEmbed) {

        if (youTubePlayerView != null) {
            youTubePlayerView.release();
        }
        playerContainer.removeAllViews();

        youTubePlayerView = new YouTubePlayerView(this);
        youTubePlayerView.setEnableAutomaticInitialization(false);
        youTubePlayerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        getLifecycle().addObserver(youTubePlayerView);


        IFramePlayerOptions.Builder iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .rel(0)
                .ivLoadPolicy(1)
                .ccLoadPolicy(1);

        EmbedConfig embedConfig = new EmbedConfig.Builder()
                .iu(iuEditText.getText().toString())
                .build();

        getLifecycle().addObserver(youTubePlayerView);

        if (isSmartEmbed) {

            iFramePlayerOptions.origin(originEditText.getText().toString());
            String[] channels = {"UC4SUWizzKc1tptprBkWjX2Q", "UCtYYXR3QV_1mKdJNksfCRtQ", "UCivB3CVSWoD5GEz6kTv2bGQ"};

            youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.play();
                }
            }, true, iFramePlayerOptions.build(), embedConfig, true, channels);
        } else {
            youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    YouTubePlayerUtils.loadOrCueVideo(
                            youTubePlayer, getLifecycle(),
                            videoIdText.getText().toString(), 0f
                    );
                }
            }, true, iFramePlayerOptions.build(), embedConfig);
        }

        playerContainer.addView(youTubePlayerView);
    }

    private void setButtonClickListener() {
        findViewById(R.id.initButton).setOnClickListener(view ->
                initYouTubePlayerView(false)
        );

        findViewById(R.id.initSmartEmbedButton).setOnClickListener(view ->
                initYouTubePlayerView(true)
        );
    }

    @Override
    protected void onDestroy() {
        if (youTubePlayerView != null) {
            youTubePlayerView.release();
        }
        super.onDestroy();
    }
}
