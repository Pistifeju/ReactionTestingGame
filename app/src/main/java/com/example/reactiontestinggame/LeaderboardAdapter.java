package com.example.reactiontestinggame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<User> users;
    private Context context;

    public LeaderboardAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.gameRecordTextView.setText(String.valueOf(user.getGameRecord()));
        if (position == 0) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
        } else if (position == 1) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
        } else if (position == 2) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView gameRecordTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            gameRecordTextView = itemView.findViewById(R.id.gameRecordTextView);
        }
    }
}

