package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        List<LeaderboardEntry> entries = dbHelper.getEntriesForUser(currentUserId);
        if (entries.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter = new LeaderboardAdapter(entries);
            recyclerView.setAdapter(adapter);
        }
    }
}
