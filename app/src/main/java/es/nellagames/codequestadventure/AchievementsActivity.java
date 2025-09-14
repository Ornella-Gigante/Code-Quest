package es.nellagames.codequestadventure;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AchievementsActivity extends AppCompatActivity {

    private AchievementsManager achievementsManager;
    private TextView tvAchievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        tvAchievements = findViewById(R.id.tvAchievements);
        achievementsManager = new AchievementsManager(this);

        StringBuilder unlocked = new StringBuilder("Unlocked Achievements:\n\n");

        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIRST_LOGIN)) {
            unlocked.append("🏆 First Login\nYou logged in for the first time.\n\n");
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIRST_CHALLENGE)) {
            unlocked.append("🎯 First Challenge Completed\nYou completed your first challenge.\n\n");
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_THREE_CORRECT_STREAK)) {
            unlocked.append("🔥 3 Correct Answers in a Row\nYou got 3 answers right consecutively.\n\n");
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIVE_GAMES_PLAYED)) {
            unlocked.append("🎉 Played 5 Games\nYou played 5 games.\n\n");
        }

        tvAchievements.setText(unlocked.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure music continues playing
        MusicService.startBackgroundMusic(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicService.pauseBackgroundMusic(this);
    }
}
