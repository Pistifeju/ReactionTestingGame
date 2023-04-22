package com.example.reactiontestinggame;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private int targetCharacterIndex = 0;
    private char[] targetCharacters;
    private long[] reactionTimes;
    private long startTime;
    private EditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        startGradientAnimation();
        initGame();
        showKeyboard();
        addKeyboardListener();
        keepKeyboardVisible();
    }

    private void initGame() {
        targetCharacters = new char[5];
        reactionTimes = new long[5];
        generateTargetCharacters();
        showNextCharacter();
    }

    private void generateTargetCharacters() {
        Random random = new Random();
        for (int i = 0; i < targetCharacters.length; i++) {
            targetCharacters[i] = (char) (random.nextInt(26) + 'A');
        }
    }

    private void showNextCharacter() {
        if (targetCharacterIndex < targetCharacters.length) {
            TextView targetCharacterTextView = findViewById(R.id.targetCharacterTextView);
            targetCharacterTextView.setText(String.valueOf(targetCharacters[targetCharacterIndex]));
            startTime = System.currentTimeMillis();
        } else {
            showResult();
        }
    }

    private void recordReactionTime() {
        long reactionTime = System.currentTimeMillis() - startTime;
        reactionTimes[targetCharacterIndex] = reactionTime;
    }

    private void showResult() {
        double averageReactionTime = 0;
        for (long reactionTime : reactionTimes) {
            averageReactionTime += reactionTime;
        }
        averageReactionTime /= reactionTimes.length;

        // Format the average reaction time to two decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedReactionTime = df.format(averageReactionTime);

        // Create an AlertDialog to show the result
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over")
                .setMessage("Your average reaction time is: " + formattedReactionTime + " ms")
                .setPositiveButton("Restart", (dialog, id) -> {
                    targetCharacterIndex = 0;
                    initGame();
                    dialog.dismiss();
                })
                .setNegativeButton("Main Menu", (dialog, id) -> {
                    finish();
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void addKeyboardListener() {
        inputEditText = findViewById(R.id.dummyEditText);
        inputEditText.requestFocus();
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString().toUpperCase();
                if (!input.isEmpty()) {
                    char pressedKey = input.charAt(input.length() - 1);
                    if (pressedKey == targetCharacters[targetCharacterIndex]) {
                        recordReactionTime();
                        targetCharacterIndex++;
                        showNextCharacter();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void keepKeyboardVisible() {
        final View activityRootView = findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            activityRootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = activityRootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight <= screenHeight * 0.15) {
                showKeyboard();
            }
        });
    }



    private void startGradientAnimation() {
        final ArgbEvaluator evaluator = new ArgbEvaluator();
        final int startColor = Color.parseColor("#FF0000");
        final int endColor = Color.parseColor("#0000FF");
        final ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(5000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(animation -> {
            float position = (float) animation.getAnimatedValue();
            int blendedColor = (Integer) evaluator.evaluate(position, startColor, endColor);

            GradientDrawable blendedGradient = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
                    new int[]{blendedColor, Color.TRANSPARENT});
            constraintLayout.setBackground(blendedGradient);
        });

        valueAnimator.start();
    }
}
