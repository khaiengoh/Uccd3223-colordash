package com.example.indiassignment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private ListView leaderboardListView;
    private LeaderboardHelper leaderboardHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardListView = findViewById(R.id.leaderboardListView);
        leaderboardHelper = new LeaderboardHelper(this);

        // Load and display the top scores
        List<LeaderboardHelper.ScoreEntry> topScores = leaderboardHelper.getScores();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        for (LeaderboardHelper.ScoreEntry score : topScores) {
            adapter.add(score.getName() + " - " + score.getScore());
        }

        leaderboardListView.setAdapter(adapter);
    }
}
