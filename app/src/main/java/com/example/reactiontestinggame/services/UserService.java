package com.example.reactiontestinggame.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.reactiontestinggame.LoginActivity;
import com.example.reactiontestinggame.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserService {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void createUser(String email, String password, String username, OnCompleteListener<Void> onCompleteListener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("users").document(userId);
                    User newUser = new User(username);
                    userRef.set(newUser).addOnCompleteListener(onCompleteListener);
                }
            } else {
                onCompleteListener.onComplete((Task<Void>) onCompleteListener);
            }
        });
    }

    public void signIn(String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener);
    }

    public void deleteUserAccount(Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        currentUser.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(context, "User account deleted successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    context.startActivity(intent);
                                    ((Activity)context).finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete user account.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to delete user data.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(context, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }
    }

}
