package es.nellagames.codequestadventure;

import android.content.ContentValues;
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

import java.io.ByteArrayOutputStream;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
        currentUserId = prefs.getLong("current_user_id", -1);

        dbHelper = new LeaderboardDbHelper(this);

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

        // Consulta corregida usando tabla "leaderboard"
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

    // Inserta usuario con avatar comprimido y redimensionado
    public long insertUserWithImage(String username, Bitmap avatarBitmap) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);

        if (avatarBitmap != null) {
            Bitmap resized = Bitmap.createScaledBitmap(avatarBitmap, 64, 64, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            byte[] avatarBytes = stream.toByteArray();

            if (avatarBytes.length > 100000) {
                stream.reset();
                resized.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                avatarBytes = stream.toByteArray();
            }

            values.put("avatar", avatarBytes);
        } else {
            values.putNull("avatar");
        }

        long result = db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return result;
    }

    private SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }

    // Actualiza la puntuación de un usuario inserta en tabla leaderboard
    public void updateUserScore(long userId, int score) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("score", score);
        values.put("timestamp", System.currentTimeMillis());

        db.insert("leaderboard", null, values);

        loadLeaderboard();
    }

    // Obtiene la puntuación máxima para un usuario
    public int getUserMaxScore(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int maxScore = 0;

        String query = "SELECT MAX(score) as max_score FROM leaderboard WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    maxScore = cursor.getInt(cursor.getColumnIndexOrThrow("max_score"));
                }
            } finally {
                cursor.close();
            }
        }

        return maxScore;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLeaderboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
