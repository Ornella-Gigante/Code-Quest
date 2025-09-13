package es.nellagames.codequestadventure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<LeaderboardEntry> entries;
    private Context context;

    public LeaderboardAdapter(Context context, List<LeaderboardEntry> entries) {
        this.context = context;
        this.entries = entries;
    }

    @NonNull
    @Override
    public LeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardEntry entry = entries.get(position);

        // Mostrar el rango/posición
        holder.rankText.setText("#" + (position + 1));

        // Mostrar nombre del jugador
        holder.playerName.setText(entry.playerName);

        // Mostrar puntuación
        holder.playerScore.setText(String.valueOf(entry.score));

        // Mostrar avatar como Bitmap
        if (entry.avatar != null) {
            holder.playerAvatar.setImageBitmap(entry.avatar);
        } else {
            // Usar avatar por defecto si no hay imagen
            holder.playerAvatar.setImageResource(R.drawable.bird); // Coincide con tu layout
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void updateData(List<LeaderboardEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView rankText;
        TextView playerName, playerScore;
        ImageView playerAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rankText);
            playerName = itemView.findViewById(R.id.playerName);
            playerScore = itemView.findViewById(R.id.playerScore);
            playerAvatar = itemView.findViewById(R.id.playerAvatar);
        }
    }
}
