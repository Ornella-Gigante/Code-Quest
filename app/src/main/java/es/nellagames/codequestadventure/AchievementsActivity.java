package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AchievementsActivity extends AppCompatActivity {

    private AchievementsManager achievementsManager;
    private TextView tvAchievements;
    private MediaPlayer victoryPlayer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        SharedPreferences prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
        long currentUserId = prefs.getLong("current_user_id", -1);

        achievementsManager = new AchievementsManager(this, currentUserId);
        tvAchievements = findViewById(R.id.tvAchievements);

        // ---- Mensaje bonito de logros ----
        StringBuilder unlocked = new StringBuilder();
        unlocked.append("✨ YOUR ACHIEVEMENTS ✨\n\n");

        boolean anyAchievement = false;
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIRST_LOGIN)) {
            unlocked.append("🏆  First Login\n   ➔ You logged in for the first time.\n\n");
            anyAchievement = true;
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIRST_CHALLENGE)) {
            unlocked.append("🎯  First Challenge Completed\n   ➔ You completed your first challenge.\n\n");
            anyAchievement = true;
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_THREE_CORRECT_STREAK)) {
            unlocked.append("🔥  Three in a Row!\n   ➔ You got 3 correct answers consecutively.\n\n");
            anyAchievement = true;
        }
        if (achievementsManager.isUnlocked(AchievementsManager.ACH_FIVE_GAMES_PLAYED)) {
            unlocked.append("🎉  Five Games Played\n   ➔ You played 5 games. Keep going!\n\n");
            anyAchievement = true;
        }
        if (!anyAchievement) {
            unlocked.append("No achievements unlocked yet.\nStart playing to earn your first badge!\n");
        }

        tvAchievements.setText(unlocked.toString());

        // ---- Botón para volver al menú principal ----
        Button backToMainButton = findViewById(R.id.backToMainButton);
        backToMainButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // ---- Música corta de victoria solo al mostrar pantalla ----
        playVictoryMusicOnce();
    }

    private void playVictoryMusicOnce() {
        victoryPlayer = MediaPlayer.create(this, R.raw.victory); // victory.mp3 debe estar en res/raw/
        if (victoryPlayer != null) {
            victoryPlayer.start();
            // Pausa y libera el MediaPlayer tras reproducir unos segundos
            handler.postDelayed(() -> {
                if (victoryPlayer.isPlaying()) {
                    victoryPlayer.stop();
                }
                victoryPlayer.release();
                victoryPlayer = null;
            }, 3200); // Dura aprox 3.2 segundos; ajusta según la duración real de tu mp3
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicService.startBackgroundMusic(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicService.pauseBackgroundMusic(this);
        // Parar música de victoria si aún suena
        if (victoryPlayer != null) {
            if (victoryPlayer.isPlaying()) {
                victoryPlayer.stop();
            }
            victoryPlayer.release();
            victoryPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}
