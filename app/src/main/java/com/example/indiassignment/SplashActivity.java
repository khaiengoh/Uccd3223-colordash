package com.example.indiassignment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.videoView);

        // Set video path from raw folder
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video);
        videoView.setVideoURI(videoUri);

        // Set listener to start the next activity once video finishes
        videoView.setOnCompletionListener(mediaPlayer -> {
            // Start MainActivity after video completes
            startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
            finish();
        });

        // Start the video
        videoView.start();
    }
}
