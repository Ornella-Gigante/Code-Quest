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

        StringBuilder unlocked = new StringBuilder("Unlocked Achievements:\n");

        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIRST_LOGIN)) {
            unlocked.append("- First Login\n");
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIRST_CHALLENGE)) {
            unlocked.append("- First Challenge Completed\n");
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_THREE_CORRECT_STREAK)) {
            unlocked.append("- 3 Correct Answers in a Row\n");
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIVE_GAMES_PLAYED)) {
            unlocked.append("- Played 5 Games\n");
        }

        tvAchievements.setText(unlocked.toString());
    }
}
