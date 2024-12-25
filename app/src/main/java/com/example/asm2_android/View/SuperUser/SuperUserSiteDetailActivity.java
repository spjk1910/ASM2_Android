package com.example.asm2_android.View.SuperUser;

import android.content.Intent;
import android.graphics.Typeface;
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
import com.example.asm2_android.View.Donor.DonorSiteDetailActivity;
import com.example.asm2_android.View.General.RegisterEventActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SuperUserSiteDetailActivity extends AppCompatActivity {
    private ImageView backButton;
    private Button generateReportButton;
    private List<String> bloodTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user_site_detail);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        String eventID = getIntent().getStringExtra("EVENT_ID");
        backButton = findViewById(R.id.ic_back);
        generateReportButton = findViewById(R.id.generate_button);

        if (eventID != null) {
            fetchEventDetails(eventID);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuperUserSiteDetailActivity.this, SuperUserReportActivity.class);
                intent.putExtra("EVENT_ID", eventID);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchEventDetails(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();

                String eventName = document.getString("eventName");
                String eventDate = document.getString("dateStart");
                String siteManagerName = document.getString("siteManagerName");
                String contactNumber = document.getString("contact");
                String locationName = document.getString("locationName");
                String eventMission = document.getString("eventMission");
                bloodTypes = (List<String>) document.get("bloodTypes");

                TextView eventNameView = findViewById(R.id.event_name);
                eventNameView.setText(eventName);

                TextView siteManagerNameView = findViewById(R.id.site_manager_name);
                siteManagerNameView.setText(siteManagerName);

                TextView contactNumberView = findViewById(R.id.contact_number);
                contactNumberView.setText(contactNumber);

                TextView locationNameView = findViewById(R.id.event_location);
                locationNameView.setText(locationName);

                TextView eventDateView = findViewById(R.id.event_date_start);
                eventDateView.setText(eventDate);

                TextView eventMissionView = findViewById(R.id.event_mission);
                eventMissionView.setText(eventMission);

                int quantityBloodType = bloodTypes.size();
                int quantityTop = 0;

                if (quantityBloodType <= 4) {
                    quantityTop = quantityBloodType;
                } else if (quantityBloodType == 5) {
                    quantityTop = 2;
                } else if (quantityBloodType == 6) {
                    quantityTop = 3;
                } else if (quantityBloodType == 7) {
                    quantityTop = 3;
                } else if (quantityBloodType == 8) {
                    quantityTop = 4;
                }


                LinearLayout bloodTypeListTop = findViewById(R.id.blood_type_list_top);
                LinearLayout bloodTypeListBottom = findViewById(R.id.blood_type_list_bottom);


                for (int i = 0; i < quantityTop; i++) {
                    RelativeLayout relativeLayout = new RelativeLayout(this);
                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                            160, 160);
                    relativeLayout.setLayoutParams(relativeParams);
                    relativeLayout.setBackgroundResource(R.drawable.blood_border);
                    relativeLayout.setGravity(Gravity.CENTER);
                    relativeLayout.setId(View.generateViewId());

                    ImageView imageView = new ImageView(this);
                    RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                            160, 160);
                    imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    imageView.setLayoutParams(imageParams);
                    imageView.setImageResource(R.drawable.ic_bloodfilled);
                    imageView.setColorFilter(ContextCompat.getColor(this, R.color.blood_orange));
                    relativeLayout.addView(imageView);

                    TextView textView = new TextView(this);
                    RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    textView.setLayoutParams(textParams);
                    textView.setText(bloodTypes.get(i));
                    textView.setTextSize(16);
                    textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    textView.setTypeface(null, Typeface.BOLD);
                    relativeLayout.addView(textView);

                    bloodTypeListTop.addView(relativeLayout);
                }

                for (int i = quantityTop; i < quantityBloodType; i++) {
                    RelativeLayout relativeLayout = new RelativeLayout(this);
                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                            160, 160);
                    relativeLayout.setLayoutParams(relativeParams);
                    relativeLayout.setBackgroundResource(R.drawable.blood_border);
                    relativeLayout.setGravity(Gravity.CENTER);
                    relativeLayout.setId(View.generateViewId());

                    ImageView imageView = new ImageView(this);
                    RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                            160, 160);
                    imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    imageView.setLayoutParams(imageParams);
                    imageView.setImageResource(R.drawable.ic_bloodfilled);
                    imageView.setColorFilter(ContextCompat.getColor(this, R.color.blood_orange));
                    relativeLayout.addView(imageView);

                    TextView textView = new TextView(this);
                    RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    textView.setLayoutParams(textParams);
                    textView.setText(bloodTypes.get(i));
                    textView.setTextSize(16);
                    textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    textView.setTypeface(null, Typeface.BOLD);
                    relativeLayout.addView(textView);

                    bloodTypeListBottom.addView(relativeLayout);
                }

            } else {
                Log.e("Firestore Error", "Error getting document: ", task.getException());
                Toast.makeText(this, "Failed to load event details.", Toast.LENGTH_SHORT).show();
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