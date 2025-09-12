package es.nellagames.codequestadventure;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private LeaderboardDbHelper dbHelper;
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new LeaderboardDbHelper(this);

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        dbHelper.resetOldEntries();
        List<LeaderboardEntry> entries = dbHelper.getAllEntries();
        if (entries.isEmpty()) {
            Toast.makeText(this, "No leaderboard entries yet!", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new LeaderboardAdapter(entries);
            recyclerView.setAdapter(adapter);
        }
    }
}
