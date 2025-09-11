package es.nellagames.codequestadventure;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Asume que tienes un Adapter para RecyclerView (crea uno simple para mostrar LeaderboardEntry)

public class LeaderboardActivity extends AppCompatActivity {
    private LeaderboardDbHelper dbHelper;
    private RecyclerView recyclerView;
    // Asume LeaderboardAdapter como tu adapter personalizado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard); // Crea este layout con RecyclerView

        dbHelper = new LeaderboardDbHelper(this);
        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        // Reset entradas antiguas (simula reset semanal)
        dbHelper.resetOldEntries();

        List<LeaderboardEntry> entries = dbHelper.getAllEntries();
        if (entries.isEmpty()) {
            Toast.makeText(this, "No entries yet!", Toast.LENGTH_SHORT).show();
        } else {
            // Configura el adapter y asigna a recyclerView
            // LeaderboardAdapter adapter = new LeaderboardAdapter(entries);
            // recyclerView.setAdapter(adapter);
        }
    }
}
