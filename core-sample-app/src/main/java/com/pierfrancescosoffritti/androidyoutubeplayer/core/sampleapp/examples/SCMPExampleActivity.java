package com.pierfrancescosoffritti.androidyoutubeplayer.core.sampleapp.examples;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.EmbedConfig;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.sampleapp.utils.VideoIdsProvider;
import com.pierfrancescosoffritti.aytplayersample.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SCMPExampleActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private FrameLayout playerContainer;
    private EditText iuEditText;
    private EditText videoIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scmp_example);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        playerContainer = findViewById(R.id.player_container);
        iuEditText = findViewById(R.id.iu_editText);
        videoIdText = findViewById(R.id.videoIdEditText);

        setButtonClickListener();
    }

    private void initYouTubePlayerView() {

        playerContainer.removeAllViews();

        youTubePlayerView = new YouTubePlayerView(this);
        youTubePlayerView.setEnableAutomaticInitialization(false);
        youTubePlayerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        getLifecycle().addObserver(youTubePlayerView);


        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(0)
                .rel(0)
                .ivLoadPolicy(1)
                .ccLoadPolicy(1)
                .build();

        EmbedConfig embedConfig = new EmbedConfig.Builder()
                .iu(iuEditText.getText().toString())
                .build();

        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer, getLifecycle(),
                        videoIdText.getText().toString(), 0f
                );
            }
        }, true, iFramePlayerOptions, embedConfig);

        playerContainer.addView(youTubePlayerView);
    }

    /**
     * Set a click listener on the "Play next video" button
     */
    private void setButtonClickListener() {
        findViewById(R.id.initButton).setOnClickListener(view ->
                initYouTubePlayerView()
        );
    }
}
