package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AchievementsActivity extends AppCompatActivity {

    private AchievementsManager achievementsManager;
    private TextView tvAchievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        SharedPreferences prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
        long currentUserId = prefs.getLong("current_user_id", -1);


        achievementsManager = new AchievementsManager(this, currentUserId);

        TextView tvAchievements = findViewById(R.id.tvAchievements);

        StringBuilder unlocked = new StringBuilder("Unlocked achievements:\n\n");

        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIRST_LOGIN)) {
            unlocked.append("🏆 First Login\nYou logged in for the first time.\n\n");
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIRST_CHALLENGE)) {
            unlocked.append("🎯 First Challenge Completed\nYou completed your first challenge.\n\n");
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_THREE_STREAK)) {
            unlocked.append("🔥 3 Correct Answers in a Row\nYou got 3 right consecutively.\n\n");
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIVE_PLAYED)) {
            unlocked.append("🎉 Played 5 Games\nYou have played 5 games.\n\n");
        }

        tvAchievements.setText(unlocked.toString());

        Button backToMainButton = findViewById(R.id.backToMainButton);
        backToMainButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

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
