package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private LeaderboardDbHelper dbHelper;
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private LinearLayout emptyView;
    private Button backToMainButton;
    private long currentUserId = -1;
    private SharedPreferences prefs;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
        currentUserId = prefs.getLong("current_user_id", -1);

        dbHelper = new LeaderboardDbHelper(this);

        soundManager = new SoundManager(this); // ← Inicializar el soundManager

        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = findViewById(R.id.emptyView);
        backToMainButton = findViewById(R.id.backToMainButton);

        backToMainButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<LeaderboardEntry> entries = new ArrayList<>();

        // Query to get users with their highest scores
        String query = "SELECT u._id AS id, u.username, u.avatar, MAX(l.score) as max_score " +
                "FROM users u " +
                "LEFT JOIN leaderboard l ON u._id = l.user_id " +
                "GROUP BY u._id, u.username, u.avatar " +
                "HAVING max_score IS NOT NULL " +
                "ORDER BY max_score DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                    byte[] avatarBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("avatar"));
                    int score = cursor.getInt(cursor.getColumnIndexOrThrow("max_score"));

                    Bitmap avatar = null;
                    if (avatarBytes != null && avatarBytes.length > 0) {
                        avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                    }

                    entries.add(new LeaderboardEntry(username, score, avatar));
                }
            } finally {
                cursor.close();
            }
        }

        // Update UI based on whether we have entries
        if (entries.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new LeaderboardAdapter(this, entries);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(entries);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundManager != null)
            soundManager.resumeBackgroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundManager != null)
            soundManager.pauseBackgroundMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null)
            soundManager.release();
    }
}
