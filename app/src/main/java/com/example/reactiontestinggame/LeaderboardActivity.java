package com.example.reactiontestinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

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
        users.clear();
        firebaseFirestore.collection("users")
                .orderBy("gameRecord", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> usersWithZeroGameRecord = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            assert user != null;
                            if (user.getGameRecord() == 0) {
                                usersWithZeroGameRecord.add(user);
                            } else {
                                users.add(user);
                            }
                        }
                        users.addAll(usersWithZeroGameRecord);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}
