package com.example.asm2_android.View.SiteManager;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.asm2_android.Model.EventDetailClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.ListOfDonorActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class SiteManagerDetailFinishedActivity extends AppCompatActivity {
    private TextView eventName,siteManagerName,contactNumber,eventLocation,
            eventDateStart,eventBloodAmount,bloodAPlus,bloodBPlus,bloodABPlus,
            bloodOPlus,bloodBMinus,bloodABMinus,bloodAMinus,bloodOMinus,eventMission;
    private ImageView backButton;
    private Button downloadDonorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_manager_detail_finished);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        EventDetailClass eventDetail = getIntent().getParcelableExtra("EVENT_DETAIL");

        if (eventDetail != null) {
            setDatatoView(eventDetail);
        }

        downloadDonorInfo = findViewById(R.id.download_button);
        backButton = findViewById(R.id.ic_back);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        downloadDonorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SiteManagerDetailFinishedActivity.this, ListOfDonorActivity.class);
                intent.putExtra("EVENT_ID", eventDetail.getEventID());
                intent.putExtra("EVENT_NAME",eventDetail.getEventName());
                startActivity(intent);
            }
        });
    }
    private void setDatatoView(EventDetailClass eventDetail) {
        eventName = findViewById(R.id.event_name);
        eventName.setText(eventDetail.getEventName());

        siteManagerName = findViewById(R.id.site_manager_name);
        siteManagerName.setText(eventDetail.getSiteManagerName());

        contactNumber = findViewById(R.id.contact_number);
        contactNumber.setText(eventDetail.getContactNumber());

        eventLocation = findViewById(R.id.event_location);
        eventLocation.setText(eventDetail.getLocationName());

        eventDateStart = findViewById(R.id.event_date_start);
        eventDateStart.setText(eventDetail.getEventDate());

        eventBloodAmount = findViewById(R.id.event_blood_amount);
        eventBloodAmount.setText(eventDetail.getTotalBloodAmount() + " ml"); //Total

        bloodAPlus = findViewById(R.id.blood_a_plus_amount);
        bloodAPlus.setText(eventDetail.getBloodTypeAPlus() + " ml"); //A+

        bloodBPlus = findViewById(R.id.blood_b_plus_amount);
        bloodBPlus.setText(eventDetail.getBloodTypeBPlus() + " ml"); //B+

        bloodABPlus = findViewById(R.id.blood_ab_plus_amount);
        bloodABPlus.setText(eventDetail.getBloodTypeABPlus() + " ml"); //AB+

        bloodOPlus = findViewById(R.id.blood_o_plus_amount);
        bloodOPlus.setText(eventDetail.getBloodTypeOPlus() + " ml"); //O+

        bloodBMinus = findViewById(R.id.blood_b_minus_amount);
        bloodBMinus.setText(eventDetail.getBloodTypeBMinus() + " ml"); //B-

        bloodABMinus = findViewById(R.id.blood_ab_minus_amount);
        bloodABMinus.setText(eventDetail.getBloodTypeABMinus() + " ml"); //AB-

        bloodAMinus = findViewById(R.id.blood_a_minus_amount);
        bloodAMinus.setText(eventDetail.getBloodTypeAMinus() + " ml"); //A-

        bloodOMinus = findViewById(R.id.blood_o_minus_amount);
        bloodOMinus.setText(eventDetail.getBloodTypeOMinus() + " ml"); //O-

        eventMission = findViewById(R.id.event_mission);
        eventMission.setText(eventDetail.getEventMission());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventDetail.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                List<String> bloodTypes = (List<String>) document.get("bloodTypes");

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