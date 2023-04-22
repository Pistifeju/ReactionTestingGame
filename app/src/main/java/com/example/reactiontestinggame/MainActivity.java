package com.example.reactiontestinggame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            logout();
        } else {
            if (user == null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userId = firebaseUser.getUid();
                DocumentReference userRef = db.collection("users").document(userId);

                userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        toolbar.setTitle(user.getUsername());
                    } else {
                        Toast.makeText(MainActivity.this, "Error happened while fetching your account.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error happened while fetching your account.", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_leaderboard) {
            showLeaderboard();
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            logout();
            return true;
        } else if (id == R.id.delete_account) {
            showDeleteAccountDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    public void startGame(View view) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void showLeaderboard() {
        Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
        startActivity(intent);
    }

    private void deleteUserAccount() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        currentUser.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(MainActivity.this, "User account deleted successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(MainActivity.this, "Failed to delete user account.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Failed to delete user data.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(MainActivity.this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Account")
                .setMessage("Do you really want to delete your account?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    deleteUserAccount();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}