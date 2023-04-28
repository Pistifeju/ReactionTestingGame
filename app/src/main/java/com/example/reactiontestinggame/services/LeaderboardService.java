package com.example.reactiontestinggame.services;
import com.example.reactiontestinggame.LeaderboardAdapter;
import com.example.reactiontestinggame.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardService {

    public void fetchUsers(List<User> users, FirebaseFirestore firebaseFirestore, LeaderboardAdapter adapter) {
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
