package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import es.nellagames.codequestadventure.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private Button btnLogin, btnCreateUser;
    private LeaderboardDbHelper dbHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        dbHelper = new LeaderboardDbHelper(this);
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
                return;
            }
            long userId = dbHelper.getUserId(username);
            if (userId > 0) {
                saveUserSession(userId, username);
            } else {
                Toast.makeText(this, "User not found. Please create a new user.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateUser.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
                return;
            }
            long userId = dbHelper.insertUser(username);
            if (userId != -1) {
                Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
                saveUserSession(userId, username);
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserSession(long userId, String username) {
        prefs.edit()
                .putLong("current_user_id", userId)
                .putString("current_username", username)
                .apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
