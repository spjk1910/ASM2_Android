package com.example.asm2_android.View.General;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.asm2_android.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DonorDetailActivity extends AppCompatActivity {
    private TextView name, username, email, address, phone, gender, birthday, blood_type;
    private ShapeableImageView avatar;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_detail);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        String currentUser = getIntent().getStringExtra("USER_ID");
        if (currentUser != null) {
            fetchUserDetails(currentUser);
        }

        backButton = findViewById(R.id.ic_back);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        gender = findViewById(R.id.gender);
        birthday = findViewById(R.id.birthday);
        blood_type = findViewById(R.id.blood_type);
        avatar = findViewById(R.id.avatar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchUserDetails(String currentUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", currentUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String currentName = document.getString("name");
                        String currentEmail = document.getString("email");
                        String currentAddress = document.getString("address");
                        String currentPhone = document.getString("phoneNumber");
                        String currentDOB = document.getString("birthday");
                        String currentBloodType = document.getString("bloodType");
                        String currentAvatar = document.getString("profileImage");
                        String currentGender = document.getString("gender");

                        name.setText(currentName);
                        username.setText(currentUser);
                        email.setText(currentEmail);
                        address.setText(currentAddress);
                        phone.setText(currentPhone);
                        gender.setText(currentGender);
                        birthday.setText(currentDOB);
                        blood_type.setText(currentBloodType);
                        if (currentAvatar != null && !currentAvatar.isEmpty()) {
                            Glide.with(getApplicationContext())
                                    .load(currentAvatar)
                                    .circleCrop()
                                    .into(avatar);
                        } else {
                            avatar.setImageResource(R.drawable.default_avatar);
                        }
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }
}