package com.example.asm2_android.View.General;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm2_android.R;

import java.util.Objects;

public class BloodSelectionActivity extends AppCompatActivity {
    private Button positiveButton, negativeButton, buttonA, buttonB, buttonO, buttonAB, saveButton;
    private ImageView backButton;
    private Button selectedBloodTypeButton = null;
    private Button selectedSignButton = null;
    private String selectedBloodType = null;
    private String selectedSign = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_selection);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        positiveButton = findViewById(R.id.buttonPositive);
        negativeButton = findViewById(R.id.buttonNegative);
        buttonA = findViewById(R.id.buttonA);
        buttonB = findViewById(R.id.buttonB);
        buttonO = findViewById(R.id.buttonO);
        buttonAB = findViewById(R.id.buttonAB);
        saveButton = findViewById(R.id.buttonSave);
        backButton = findViewById(R.id.ic_back);

        View.OnClickListener bloodTypeListener = v -> {
            Button clickedButton = (Button) v;
            if (selectedBloodTypeButton == clickedButton) {
                clickedButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.bubble_gum));
                selectedBloodTypeButton = null;
                selectedBloodType = null;
            } else {
                if (selectedBloodTypeButton != null) {
                    selectedBloodTypeButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.bubble_gum));
                }
                clickedButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blood_orange));
                selectedBloodTypeButton = clickedButton;
                selectedBloodType = clickedButton.getText().toString();
            }
        };

        buttonA.setOnClickListener(bloodTypeListener);
        buttonB.setOnClickListener(bloodTypeListener);
        buttonO.setOnClickListener(bloodTypeListener);
        buttonAB.setOnClickListener(bloodTypeListener);

        View.OnClickListener signListener = v -> {
            Button clickedButton = (Button) v;
            if (selectedSignButton == clickedButton) {
                clickedButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.bubble_gum));
                selectedSignButton = null;
                selectedSign = null;
            } else {
                if (selectedSignButton != null) {
                    selectedSignButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.bubble_gum));
                }
                clickedButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blood_orange));
                selectedSignButton = clickedButton;
                selectedSign = clickedButton.getText().toString();
            }
        };

        positiveButton.setOnClickListener(signListener);
        negativeButton.setOnClickListener(signListener);

        saveButton.setOnClickListener(v -> {
            if (checkIfReadyToSave()) {
                if (selectedBloodType != null && selectedSign != null) {
                    String fullBloodType = selectedBloodType + selectedSign;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedBloodType", fullBloodType);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Boolean checkIfReadyToSave() {
        if (selectedBloodType != null && selectedSign != null) {
            return true;
        } else {
            Toast.makeText(this, "Please select both blood type and sign", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }
}