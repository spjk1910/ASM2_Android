package com.example.asm2_android.View.General;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm2_android.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class FinishedEventActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView location,sitePhone;
    private Button contactButton;
    private Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_event);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        String eventID = getIntent().getStringExtra("EVENT_ID");
        String donorName = getIntent().getStringExtra("DONOR_NAME");
        String bloodType = getIntent().getStringExtra("BLOOD_TYPE");
        String bloodVolume = getIntent().getStringExtra("BLOOD_VOLUME");

        backButton = findViewById(R.id.ic_back);
        location = findViewById(R.id.site_address);
        contactButton = findViewById(R.id.contact_button);
        sitePhone = findViewById(R.id.site_phone);

        if (eventID != null) {
            fetchEventDetails(eventID, bloodVolume,bloodType, donorName);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinishedEventActivity.this, MapActivity.class);
                intent.putExtra("LOCATION_NAME",location.getText().toString());
                intent.putExtra("LATITUDE",latitude);
                intent.putExtra("LONGITUDE",longitude);
                startActivity(intent);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = sitePhone.getText().toString();
                String phoneNumber = "tel:" + phone;

                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse(phoneNumber));

                startActivity(dialIntent);
            }
        });

    }

    private void fetchEventDetails(String eventId,String bloodVolume, String bloodType, String donorName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();

                String eventName = document.getString("eventName");
                String eventDate = document.getString("dateStart");
                String contactNumber = document.getString("contact");
                String locationName = document.getString("locationName");
                latitude = document.getDouble("latitude");
                longitude = document.getDouble("longitude");

                TextView bloodTypeView = findViewById(R.id.blood_type);
                bloodTypeView.setText(bloodType);

                TextView donorNameView = findViewById(R.id.donor_name);
                donorNameView.setText(donorName);

                TextView eventNameView = findViewById(R.id.event_name);
                eventNameView.setText(eventName);

                TextView locationNameView = findViewById(R.id.site_address);
                locationNameView.setText(locationName);

                TextView bloodVolumeView = findViewById(R.id.blood_volume);
                bloodVolumeView.setText(bloodVolume);

                TextView contactNumberView = findViewById(R.id.site_phone);
                contactNumberView.setText(contactNumber);

                TextView eventDateView = findViewById(R.id.event_date);
                eventDateView.setText(eventDate);
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
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                );
            }
        }
    }