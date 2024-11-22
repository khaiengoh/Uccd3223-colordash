package com.example.indiassignment;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LeaderboardHelper {
    private static final String PREF_NAME = "Leaderboard";
    private static final int MAX_SCORES = 25;
    private SharedPreferences sharedPreferences;
    private Random random;

    // List of random names to use for the leaderboard
    private String[] randomNames = {
            "Elon Musk", "Bill Gates", "Steve Jobs", "Mark Zuckerberg", "Jeff Bezos",
            "Tim Cook", "Sundar Pichai", "Satya Nadella", "Larry Page", "Sergey Brin",
            "Warren Buffet", "Richard Branson", "Jack Ma", "Sheryl Sandberg", "Oprah Winfrey",
            "Ellen DeGeneres", "Beyoncé", "Taylor Swift", "Kanye West", "Rihanna",
            "Dwayne Johnson", "Kevin Hart", "Will Smith", "Chris Hemsworth", "Brad Pitt",
            "Leonardo DiCaprio"
    };

    public LeaderboardHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        random = new Random();
    }

    // Save a score for the leaderboard (Top 25 players)
    public void saveScore(String name, int score) {
        List<ScoreEntry> scores = getScores();
        scores.add(new ScoreEntry(name, score));

        // Sort scores in descending order and keep only the top 25
        scores.sort((a, b) -> b.getScore() - a.getScore());
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES); // Trim to top 25 scores
        }

        // Save the updated scores to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("all_scores", serializeScores(scores));
        editor.apply();
    }

    // Get all combined scores (Top 25 players)
    public List<ScoreEntry> getScores() {
        String serializedScores = sharedPreferences.getString("all_scores", "");
        if (serializedScores.isEmpty()) {
            generateRandomLeaderboard();  // Generate random scores if none exist
            return getScores();
        }
        return deserializeScores(serializedScores);
    }

    // Generate random leaderboard data with scores between 30 and 45 for top 25 players
    private void generateRandomLeaderboard() {
        List<ScoreEntry> randomScores = new ArrayList<>();
        for (int i = 0; i < MAX_SCORES; i++) {
            String randomName = randomNames[random.nextInt(randomNames.length)];
            int randomScore = random.nextInt(16) + 30; // Random score between 30 and 45
            randomScores.add(new ScoreEntry(randomName, randomScore));
        }

        // Save the generated random scores
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("all_scores", serializeScores(randomScores));
        editor.apply();
    }

    // Serialize the list of scores to a String
    private String serializeScores(List<ScoreEntry> scores) {
        StringBuilder sb = new StringBuilder();
        for (ScoreEntry score : scores) {
            sb.append(score.getName()).append(":").append(score.getScore()).append(",");
        }
        return sb.toString();
    }

    // Deserialize the scores from a String
    private List<ScoreEntry> deserializeScores(String serializedScores) {
        List<ScoreEntry> scores = new ArrayList<>();
        String[] scoreArray = serializedScores.split(",");
        for (String scoreStr : scoreArray) {
            if (!scoreStr.isEmpty()) {
                String[] parts = scoreStr.split(":");
                String name = parts[0];
                int score = Integer.parseInt(parts[1]);
                scores.add(new ScoreEntry(name, score));
            }
        }
        // Sort scores in descending order
        scores.sort((a, b) -> b.getScore() - a.getScore());
        return scores;
    }

    // Check if the given score is among the top 25 scores
    public boolean isTopScore(int score) {
        List<ScoreEntry> scores = getScores();
        if (scores.size() < MAX_SCORES) {
            return true; // Automatically qualifies if fewer than 25 scores
        }
        return score > scores.get(scores.size() - 1).getScore(); // Higher than the lowest score in top 25
    }

    // Class to represent a score entry (name, score)
    public static class ScoreEntry {
        private String name;
        private int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
}
