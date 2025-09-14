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
    private Button btnLogin, btnCreateUser, btnClearUsers;
    private SharedPreferences prefs;
    private UserDataManager userDataManager;
    private LeaderboardDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Start background music service
        startService(new Intent(this, MusicService.class));

        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
        userDataManager = new UserDataManager(this);
        dbHelper = new LeaderboardDbHelper(this);

        // Clean up corrupted data on first run
        performFirstRunCleanup();

        // Check if user is already logged in
        if (prefs.contains("current_user_id")) {
            long userId = prefs.getLong("current_user_id", -1);
            if (userId != -1) {
                // Validate and repair user data if needed
                if (!userDataManager.validateUserData(userId)) {
                    userDataManager.repairUserData(userId);
                }

                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }
        }

        initializeViews();
        setupListeners();
    }

    private void performFirstRunCleanup() {
        boolean firstRun = prefs.getBoolean("first_run", true);
        if (firstRun) {
            // Clear potentially corrupted user data from previous versions
            LeaderboardDbHelper dbHelper = new LeaderboardDbHelper(this);

            // Only clear if there are any database issues
            try {
                // Test database integrity
                dbHelper.getReadableDatabase();
            } catch (Exception e) {
                // If database has issues, clear it
                dbHelper.clearAllUsers();
                Toast.makeText(this, "Database cleaned for first run", Toast.LENGTH_SHORT).show();
            } finally {
                dbHelper.close();
            }

            prefs.edit().putBoolean("first_run", false).apply();
        }
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        btnClearUsers = findViewById(R.id.btnClearUsers); // Nuevo botón
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        btnCreateUser.setOnClickListener(v -> startActivity(new Intent(this, CreateUserActivity.class)));

        // Listener para borrar todos los usuarios
        btnClearUsers.setOnClickListener(v -> {
            dbHelper.clearAllUsers();
            Toast.makeText(this, "Todos los usuarios han sido borrados.", Toast.LENGTH_LONG).show();
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        LeaderboardDbHelper db = new LeaderboardDbHelper(this);

        try {
            Cursor cursor = db.getUserCursor(username);

            if (cursor != null && cursor.moveToFirst()) {
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));

                // Migrate any old global data to this user's data
                userDataManager.migrateGlobalDataToUser(userId);

                // Validate and repair user data if needed
                if (!userDataManager.validateUserData(userId)) {
                    userDataManager.repairUserData(userId);
                    Toast.makeText(this, "User data repaired", Toast.LENGTH_SHORT).show();
                }

                // Save login session
                prefs.edit()
                        .putLong("current_user_id", userId)
                        .putString("current_username", username)
                        .putString("current_avatar", "") // Kept for compatibility
                        .apply();

                cursor.close();

                // Show welcome back message with user stats
                UserDataManager.UserGameData userData = userDataManager.getUserGameData(userId);
                String welcomeMessage = "Welcome back! Score: " + userData.score +
                        ", Progress: " + userData.completedPieces + "/10";
                Toast.makeText(this, welcomeMessage, Toast.LENGTH_LONG).show();

                startActivity(new Intent(this, MainActivity.class));
                finish();

            } else {
                Toast.makeText(this, "User not found. Please create a new user.", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) {
                cursor.close();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear the username field when returning to login
        if (etUsername != null) {
            etUsername.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
    }
}
