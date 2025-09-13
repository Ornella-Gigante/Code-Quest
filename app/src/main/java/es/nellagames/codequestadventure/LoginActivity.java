package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private Button btnLogin;
    private SoundManager soundManager;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences("Codequest", MODE_PRIVATE);

        soundManager = new SoundManager(this);
        soundManager.startBackgroundMusic();

        etUsername = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);

        // Check if user already logged in
        if (prefs.contains("username")) {
            navigateToMain();
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString("username", username).apply();
            Toast.makeText(this, "Welcome " + username, Toast.LENGTH_SHORT).show();
            navigateToMain();
            finish();
        });
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundManager != null) soundManager.resumeBackgroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundManager != null) soundManager.pauseBackgroundMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) soundManager.release();
    }
}
