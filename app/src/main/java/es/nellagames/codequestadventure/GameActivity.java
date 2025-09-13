package es.nellagames.codequestventure;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    // ... otras variables ...

    private long currentUserId = -1;
    private String currentUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
        currentUserId = prefs.getLong("current_user_id", -1);
        currentUsername = prefs.getString("current_username", "Guest");

        // ... resto de código ...

        // ejemplo de dónde guardar score:
        LeaderboardDbHelper dbHelper = new LeaderboardDbHelper(this);
        if (currentUserId != -1 && score > 0) {
            dbHelper.insertEntry(currentUserId, score, "");
        }
    }

    // ... resto de código (asegura pasar currentUserId y currentUsername donde lo necesites)
}
