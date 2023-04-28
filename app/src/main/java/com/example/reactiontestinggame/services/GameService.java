package com.example.reactiontestinggame.services;

import com.example.reactiontestinggame.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GameService {
    public void saveGameResult(double averageReactionTime, User user, Runnable onSuccess, OnFailureListener onFailureListener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

            Map<String, Object> updates = new HashMap<>();
            updates.put("games", FieldValue.arrayUnion(averageReactionTime));

            if (averageReactionTime < user.getGameRecord() || user.getGameRecord() == 0) {
                updates.put("gameRecord", averageReactionTime);
                user.setGameRecord((float) averageReactionTime);
            }

            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        onSuccess.run();
                    })
                    .addOnFailureListener(onFailureListener);
        } else {
            onFailureListener.onFailure(new Exception("Firebase user is null."));
        }
    }

}
