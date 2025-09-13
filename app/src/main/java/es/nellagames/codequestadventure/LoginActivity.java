package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private Button btnLogin, btnCreateUser;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startService(new Intent(this, MusicService.class));

        // SOLO LA PRIMERA VEZ: limpia usuarios viejos problemáticos (después de verificar, eliminar)
        LeaderboardDbHelper dbTemp = new LeaderboardDbHelper(this);
        dbTemp.clearAllUsers();
        dbTemp.close();

        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        // Si ya hay sesión iniciada, ir a MainActivity
        if (prefs.contains("current_user_id")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateUser = findViewById(R.id.btnCreateUser);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
                return;
            }

            LeaderboardDbHelper db = new LeaderboardDbHelper(this);
            Cursor c = db.getUserCursor(username);

            if (c.moveToFirst()) {
                long userId = c.getLong(c.getColumnIndexOrThrow("_id"));

                // Ya no hace falta convertir avatar a string, pero mantenemos compatibilidad
                prefs.edit()
                        .putLong("current_user_id", userId)
                        .putString("current_username", username)
                        .putString("current_avatar", "")
                        .apply();

                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "User not found. Please create user.", Toast.LENGTH_SHORT).show();
            }
            c.close();
            db.close();
        });

        btnCreateUser.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateUserActivity.class));
        });
    }
}
