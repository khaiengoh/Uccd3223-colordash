package com.example.indiassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import android.net.Uri;
import android.widget.Toast;
import android.media.AudioManager;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    private View leaderboardSelectionLayout;

    public static final String EXTRA_SHOW_LEADERBOARD = "show_leaderboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        leaderboardSelectionLayout = findViewById(R.id.leaderboardButton);

        // Set up background video
        VideoView videoView = findViewById(R.id.videoBackground);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
        videoView.setVideoURI(videoUri);

        Button howToPlayButton = findViewById(R.id.howToPlayButton);
        howToPlayButton.setOnClickListener(v -> showHowToPlayDialog());

        // Request audio focus to play audio
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.requestAudioFocus(
                    focusChange -> {
                        // Handle audio focus changes if needed
                    },
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
            );
        }

        // Set the video to loop continuously
        videoView.setOnCompletionListener(mediaPlayer -> mediaPlayer.start());

        // Set up an OnPreparedListener to handle video and audio playback
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Set the audio to maximum volume (both channels)
                mp.setVolume(4.0f, 4.0f);  // Maximum volume for both left and right channels
                videoView.start();  // Start video playback
            }
        });

        // Start the video playback
        videoView.start();

        // Check if we should display the leaderboard
        if (getIntent().getBooleanExtra(EXTRA_SHOW_LEADERBOARD, false)) {
            showLeaderboard();  // Automatically display leaderboard
        }

        // Start button to begin from level 1
        findViewById(R.id.startButton).setOnClickListener(v -> startLevel(1));

        // Leaderboard button to display the combined leaderboard
        findViewById(R.id.leaderboardButton).setOnClickListener(v -> viewLeaderboard());

        // Find the Exit button and set an OnClickListener to finish the activity
        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            finish(); // Close the current activity (effectively exiting the app)
        });
    }

    private void startLevel(int level) {
        Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    private void showHowToPlayDialog() {
        new AlertDialog.Builder(this)
                .setTitle("How to Play")
                .setMessage("• The goal is to touch the highlighted view as quickly as possible.\n\n" +
                        "• Each level has a grid with a specific number of views:\n" +
                        "  - Level 1: 4 Views\n" +
                        "  - Level 2: 9 Views\n" +
                        "  - Level 3: 16 Views\n" +
                        "  - Level 4: 25 Views\n" +
                        "  - Level 5: 36 Views\n\n" +
                        "• Touch the highlighted view to score points. After each correct touch, a new view will be highlighted.\n\n" +
                        "• Each level lasts 5 seconds and advances automatically.\n\n" +
                        "• If your score is in the top 25, you can enter your name in the leaderboard.")
                .setPositiveButton("Got it!", null)
                .setCancelable(true)
                .show();
    }

    private void viewLeaderboard() {
        LeaderboardHelper leaderboardHelper = new LeaderboardHelper(this);
        List<LeaderboardHelper.ScoreEntry> scores = leaderboardHelper.getScores();

        StringBuilder leaderboardText = new StringBuilder();
        for (int i = 0; i < scores.size(); i++) {
            LeaderboardHelper.ScoreEntry entry = scores.get(i);
            leaderboardText.append((i + 1)).append(". ").append(entry.getName())
                    .append(" - ").append(entry.getScore()).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Leaderboard")
                .setMessage(leaderboardText.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLeaderboard() {
        // Code to display leaderboard, e.g., opening Leaderboard dialog or fragment
        Toast.makeText(this, "You are in the leaderboard!", Toast.LENGTH_SHORT).show();
    }
}
