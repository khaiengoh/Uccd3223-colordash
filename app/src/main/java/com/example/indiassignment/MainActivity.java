package com.example.indiassignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView scoreTextView, countdownTextView, readyCountdownTextView;
    private GridLayout gridLayout;
    private List<View> gameViews = new ArrayList<>();
    private int currentLevel = 1;  // Start at level 1
    private int score = 0;
    private View highlightedView;
    private Random random = new Random();
    private CountDownTimer countDownTimer;
    private LeaderboardHelper leaderboardHelper;
    private MediaPlayer clickSoundPlayer;
    private MediaPlayer mediaPlayer;
    private MediaPlayer backgroundMusicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.scoreTextView);
        countdownTextView = findViewById(R.id.countdownTextView);
        readyCountdownTextView = findViewById(R.id.readyCountdownTextView);
        gridLayout = findViewById(R.id.gridLayout);
        leaderboardHelper = new LeaderboardHelper(this);

        // Initialize the click sound player
        clickSoundPlayer = MediaPlayer.create(this, R.raw.click_sound);

        // Initialize and start background music
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.game_music);
        backgroundMusicPlayer.setLooping(true);  // Loop the music
        backgroundMusicPlayer.start();

        showStartDialog();  // Show start dialog instead of level selection
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources when the activity is destroyed
        if (clickSoundPlayer != null) {
            clickSoundPlayer.release();
            clickSoundPlayer = null;
        }
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }
    }


    private void showStartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Level 1")
                .setMessage("Get ready! Click start to begin level 1")
                .setPositiveButton("Start", (dialog, which) -> startReadyCountdown())
                .setCancelable(false)
                .show();
    }

    private void setupLevel(int level) {
        gridLayout.removeAllViews();
        gameViews.clear();

        int gridSize;
        switch (level) {
            case 1: gridSize = 2; break;
            case 2: gridSize = 3; break;
            case 3: gridSize = 4; break;
            case 4: gridSize = 5; break;
            case 5: gridSize = 6; break;
            default: gridSize = 2;
        }

        gridLayout.setColumnCount(gridSize);

        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int gridWidth = gridLayout.getWidth();
                int gridHeight = gridLayout.getHeight();
                int boxSize = Math.min(
                        (gridWidth - (gridSize + 1) * 16) / gridSize,
                        (gridHeight - (gridSize + 1) * 16) / gridSize
                );

                for (int i = 0; i < gridSize * gridSize; i++) {
                    View view = new View(MainActivity.this);
                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                    layoutParams.width = boxSize;
                    layoutParams.height = boxSize;
                    layoutParams.setMargins(8, 8, 8, 8);

                    view.setLayoutParams(layoutParams);
                    view.setBackgroundColor(Color.CYAN);

                    view.setOnClickListener(v -> {
                        if (v == highlightedView && gridLayout.isClickable()) {
                            score++;
                            scoreTextView.setText("Score: " + score);

                            // Play the click sound instantly
                            playClickSound();

                            highlightRandomView();
                        }
                    });

                    gameViews.add(view);
                    gridLayout.addView(view);
                }

                highlightRandomView();
            }
        });
    }

    private void startReadyCountdown() {
        // Setup the level and disable grid click
        setupLevel(currentLevel);
        gridLayout.setClickable(false);

        // Initialize MediaPlayer for countdown sound
        mediaPlayer = MediaPlayer.create(this, R.raw.readyfight);
        mediaPlayer.start();

        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                readyCountdownTextView.setText("Ready in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                readyCountdownTextView.setText("");
                gridLayout.setClickable(true);
                startInGameTimer();
            }
        }.start();

        // Release media player resources when done
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            mediaPlayer = null;
        });
    }

    private void startInGameTimer() {
        countDownTimer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTextView.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                countdownTextView.setText("Time: 0");

                // After the time is up, proceed to the next level or end the game
                if (currentLevel < 5) {
                    showReadyDialogForNextLevel();
                } else {
                    showFinalScoreDialog();  // End the game after level 4
                }
            }
        };
        countDownTimer.start();
    }

    private void playClickSound() {
        if (clickSoundPlayer != null) {
            if (clickSoundPlayer.isPlaying()) {
                clickSoundPlayer.pause();  // Pause if still playing to avoid overlapping sounds
                clickSoundPlayer.seekTo(0);  // Reset to the beginning
            }
            clickSoundPlayer.start();  // Play the sound
        }
    }

    private void showReadyDialogForNextLevel() {
        // Initialize MediaPlayer for the victory sound
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.party_horn);
        mediaPlayer.start();

        // Release resources after the sound is done playing
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
        });

        new AlertDialog.Builder(this)
                .setTitle("Level " + (currentLevel + 1))
                .setMessage("Ready for Level " + (currentLevel + 1) + "? Proceed to the next level?")
                .setPositiveButton("Proceed", (dialog, which) -> {
                    currentLevel++;
                    startReadyCountdown();  // Proceed to the next level
                })
                .setNegativeButton("Exit Game", (dialog, which) -> showFinalScoreDialog())
                .setCancelable(false)
                .show();
    }


    private void highlightRandomView() {
        if (highlightedView != null) {
            highlightedView.setBackgroundColor(Color.CYAN);
        }

        int randomIndex = random.nextInt(gameViews.size());
        highlightedView = gameViews.get(randomIndex);
        highlightedView.setBackgroundColor(Color.YELLOW);
    }

    private void showFinalScoreDialog() {
        if (leaderboardHelper.isTopScore(score)) {
            // Initialize MediaPlayer for the party horn sound
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.goodresult);
            mediaPlayer.start();

            // Play the sound and release resources when done
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
            });

            // Prompt the user for their name if they qualify for the leaderboard
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New High Score!");
            builder.setMessage("Congratulations! Enter your name for the leaderboard:");

            final EditText input = new EditText(this);
            builder.setView(input);

            builder.setCancelable(false); // Prevent the dialog from being canceled by the back button

            builder.setPositiveButton("Submit", null); // Set a placeholder for the button
            AlertDialog dialog = builder.create();

            dialog.setOnShowListener(d -> {
                Button submitButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                submitButton.setOnClickListener(v -> {
                    String playerName = input.getText().toString().trim();
                    if (!playerName.isEmpty()) {
                        leaderboardHelper.saveScore(playerName, score);
                        returnToMainMenuWithToast();
                        dialog.dismiss(); // Dismiss only if the input is valid
                    } else {
                        Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            dialog.show();
        } else {
            // Show "Great job" dialog if score is not a top score
            new AlertDialog.Builder(this)
                    .setTitle("Great Job!")
                    .setMessage("Great job, your score is " + score)
                    .setPositiveButton("OK", (dialog, which) -> returnToMainMenu())
                    .setCancelable(false)
                    .show();
        }
    }




    // Display leaderboard view and toast
    private void returnToMainMenuWithToast() {
        stopBackgroundMusic();  // Stop music when returning to main menu
        Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
        intent.putExtra(MainMenuActivity.EXTRA_SHOW_LEADERBOARD, true);  // Set the flag
        startActivity(intent);
        finish();
    }

    private void returnToMainMenu() {
        stopBackgroundMusic();  // Stop music when returning to main menu
        Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }
    }
}
