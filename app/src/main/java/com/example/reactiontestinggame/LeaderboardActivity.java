package com.example.reactiontestinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.example.reactiontestinggame.services.LeaderboardService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button backButton, refreshButton;
    private List<User> users;
    private LeaderboardAdapter adapter;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        users = new ArrayList<>();
        adapter = new LeaderboardAdapter(this, users);
        recyclerView.setAdapter(adapter);
        firebaseFirestore = FirebaseFirestore.getInstance();

        backButton.setOnClickListener(view -> finish());

        refreshButton.setOnClickListener(view -> fetchUsers());

        fetchUsers();
    }

    private void fetchUsers() {
        LeaderboardService leaderboardService = new LeaderboardService();
        leaderboardService.fetchUsers(users, firebaseFirestore, adapter);
    }
}
