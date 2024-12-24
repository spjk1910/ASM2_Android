package com.example.asm2_android.View.SiteManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.asm2_android.Model.EventDetailClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.ChooseLocationActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SiteManagerModifySiteEventDetailActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> chooseLocationLauncher;
    private EditText eventName,contactNumber,eventMission;
    private TextView eventLocation,siteManagerName,eventBloodAmount,eventDateStart,bloodAPlus,bloodBPlus,bloodABPlus,
            bloodOPlus,bloodBMinus,bloodABMinus,bloodAMinus,bloodOMinus,clearButton;
    private LinearLayout bloodTypeList,bloodTypeListTop,bloodTypeListBottom;
    private ImageView backButton,closeButton;
    private Button updateButton,saveButton;
    private List<String> bloodTypes;

    private Dialog bloodSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_site_event_detail);
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

        updateButton = findViewById(R.id.update_button);
        backButton = findViewById(R.id.ic_back);
        bloodTypeList = findViewById(R.id.blood_type_list);
        bloodTypeListTop = findViewById(R.id.blood_type_list_top);
        bloodTypeListBottom = findViewById(R.id.blood_type_list_bottom);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bloodSelection = new Dialog(SiteManagerModifySiteEventDetailActivity.this);
        bloodSelection.setContentView(R.layout.blood_selection_sheet);
        bloodSelection.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bloodSelection.getWindow().setBackgroundDrawable(getDrawable(R.drawable.filter_sheet_bg));
        bloodSelection.setCancelable(false);

        saveButton = bloodSelection.findViewById(R.id.buttonSave);
        clearButton = bloodSelection.findViewById(R.id.clearButton);
        closeButton = bloodSelection.findViewById(R.id.close_filter);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox aPlusCheckBox = bloodSelection.findViewById(R.id.checkbox_A_plus);
                CheckBox aMinusCheckBox = bloodSelection.findViewById(R.id.checkbox_A_minus);
                CheckBox bPlusCheckBox = bloodSelection.findViewById(R.id.checkbox_B_plus);
                CheckBox bMinusCheckBox = bloodSelection.findViewById(R.id.checkbox_B_minus);
                CheckBox oPlusCheckBox = bloodSelection.findViewById(R.id.checkbox_O_plus);
                CheckBox oMinusCheckBox = bloodSelection.findViewById(R.id.checkbox_O_minus);
                CheckBox abPlusCheckBox = bloodSelection.findViewById(R.id.checkbox_AB_plus);
                CheckBox abMinusCheckBox = bloodSelection.findViewById(R.id.checkbox_AB_minus);

                aPlusCheckBox.setChecked(false);
                aMinusCheckBox.setChecked(false);
                bPlusCheckBox.setChecked(false);
                bMinusCheckBox.setChecked(false);
                oPlusCheckBox.setChecked(false);
                oMinusCheckBox.setChecked(false);
                abPlusCheckBox.setChecked(false);
                abMinusCheckBox.setChecked(false);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodSelection.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAPlusChecked = ((CheckBox) bloodSelection.findViewById(R.id.checkbox_A_plus)).isChecked();
                boolean isAMinusChecked = ((CheckBox) bloodSelection.findViewById(R.id.checkbox_A_minus)).isChecked();
                boolean isBPlusChecked = ((CheckBox) bloodSelection.findViewById(R.id.checkbox_B_plus)).isChecked();
                boolean isBMinusChecked = ((CheckBox) bloodSelection.findViewById(R.id.checkbox_B_minus)).isChecked();
                boolean isOPlusChecked = ((CheckBox) bloodSelection.findViewById(R.id.checkbox_O_plus)).isChecked();
                boolean isOMinusChecked = ((CheckBox) bloodSelection.findViewById(R.id.checkbox_O_minus)).isChecked();
                boolean isABPlusChecked = ((CheckBox) bloodSelection.findViewById(R.id.checkbox_AB_plus)).isChecked();
                boolean isABMinusChecked = ((CheckBox) bloodSelection.findViewById(R.id.checkbox_AB_minus)).isChecked();

                bloodTypes = new ArrayList<>();
                if (isAPlusChecked) bloodTypes.add("A+");
                if (isAMinusChecked) bloodTypes.add("A-");
                if (isBPlusChecked) bloodTypes.add("B+");
                if (isBMinusChecked) bloodTypes.add("B-");
                if (isOPlusChecked) bloodTypes.add("O+");
                if (isOMinusChecked) bloodTypes.add("O-");
                if (isABPlusChecked) bloodTypes.add("AB+");
                if (isABMinusChecked) bloodTypes.add("AB-");

                if (!bloodTypes.isEmpty()) {
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

                    bloodTypeListTop.removeAllViews();
                    bloodTypeListBottom.removeAllViews();

                    for (int i = 0; i < quantityTop; i++) {
                        RelativeLayout relativeLayout = new RelativeLayout(SiteManagerModifySiteEventDetailActivity.this);
                        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                                160, 160);
                        relativeLayout.setLayoutParams(relativeParams);
                        relativeLayout.setBackgroundResource(R.drawable.blood_border);
                        relativeLayout.setGravity(Gravity.CENTER);
                        relativeLayout.setId(View.generateViewId());

                        ImageView imageView = new ImageView(SiteManagerModifySiteEventDetailActivity.this);
                        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                                160, 160);
                        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        imageView.setLayoutParams(imageParams);
                        imageView.setImageResource(R.drawable.ic_bloodfilled);
                        imageView.setColorFilter(ContextCompat.getColor(SiteManagerModifySiteEventDetailActivity.this, R.color.blood_orange));
                        relativeLayout.addView(imageView);

                        TextView textView = new TextView(SiteManagerModifySiteEventDetailActivity.this);
                        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        textView.setLayoutParams(textParams);
                        textView.setText(bloodTypes.get(i));
                        textView.setTextSize(16);
                        textView.setTextColor(ContextCompat.getColor(SiteManagerModifySiteEventDetailActivity.this, android.R.color.white));
                        textView.setTypeface(null, Typeface.BOLD);
                        relativeLayout.addView(textView);

                        bloodTypeListTop.addView(relativeLayout);
                    }

                    for (int i = quantityTop; i < quantityBloodType; i++) {
                        RelativeLayout relativeLayout = new RelativeLayout(SiteManagerModifySiteEventDetailActivity.this);
                        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                                160, 160);
                        relativeLayout.setLayoutParams(relativeParams);
                        relativeLayout.setBackgroundResource(R.drawable.blood_border);
                        relativeLayout.setGravity(Gravity.CENTER);
                        relativeLayout.setId(View.generateViewId());

                        ImageView imageView = new ImageView(SiteManagerModifySiteEventDetailActivity.this);
                        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                                160, 160);
                        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        imageView.setLayoutParams(imageParams);
                        imageView.setImageResource(R.drawable.ic_bloodfilled);
                        imageView.setColorFilter(ContextCompat.getColor(SiteManagerModifySiteEventDetailActivity.this, R.color.blood_orange));
                        relativeLayout.addView(imageView);

                        TextView textView = new TextView(SiteManagerModifySiteEventDetailActivity.this);
                        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        textView.setLayoutParams(textParams);
                        textView.setText(bloodTypes.get(i));
                        textView.setTextSize(16);
                        textView.setTextColor(ContextCompat.getColor(SiteManagerModifySiteEventDetailActivity.this, android.R.color.white));
                        textView.setTypeface(null, Typeface.BOLD);
                        relativeLayout.addView(textView);

                        bloodTypeListBottom.addView(relativeLayout);
                    }
                }

                bloodSelection.dismiss();
            }
        });

        bloodTypeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodSelection.show();
            }
        });

        eventDateStart = findViewById(R.id.event_date_start);
        eventDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SiteManagerModifySiteEventDetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);

                                SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd.yyyy", Locale.getDefault());
                                String formattedDate = sdf.format(selectedCalendar.getTime());

                                eventDateStart.setText(formattedDate);
                            }
                        },
                        year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SiteManagerModifySiteEventDetailActivity.this, ChooseLocationActivity.class);
                chooseLocationLauncher.launch(intent);
            }
        });

        chooseLocationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String locationName = data.getStringExtra("locationName");

                            Double latitude = data.getDoubleExtra("latitude", 0.0);
                            Double longitude = data.getDoubleExtra("longitude", 0.0);

                            TextView latitudeHolder = findViewById(R.id.latitude_holder);
                            TextView longitudeHolder = findViewById(R.id.longitude_holder);
                            latitudeHolder.setText(String.valueOf(latitude));
                            longitudeHolder.setText(String.valueOf(longitude));
                            eventLocation.setText(locationName);
                        }

                    }
                }
        );

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("events")
                        .document(eventDetail.getEventID())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    Map<String, Object> updatedFields = new HashMap<>();
                                    TextView latitudeHolder = findViewById(R.id.latitude_holder);
                                    TextView longitudeHolder = findViewById(R.id.longitude_holder);
                                    if (latitudeHolder != null && longitudeHolder != null
                                            && !latitudeHolder.getText().toString().trim().isEmpty()
                                            && !longitudeHolder.getText().toString().trim().isEmpty()) {
                                        try {
                                            updatedFields.put("latitude", Double.parseDouble(latitudeHolder.getText().toString().trim()));
                                            updatedFields.put("longitude", Double.parseDouble(longitudeHolder.getText().toString().trim()));
                                        } catch (NumberFormatException e) {
                                            Toast.makeText(SiteManagerModifySiteEventDetailActivity.this, "Invalid latitude or longitude value. Please enter valid numbers.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                       // Toast.makeText(SiteManagerModifySiteEventDetailActivity.this, "Latitude or longitude cannot be empty.", Toast.LENGTH_SHORT).show();
                                    }


                                    updatedFields.put("eventName", eventName.getText().toString());
                                    updatedFields.put("contact", contactNumber.getText().toString());
                                    updatedFields.put("locationName", eventLocation.getText().toString());
                                    updatedFields.put("dateStart", eventDateStart.getText().toString());
                                    updatedFields.put("bloodTypeAPlus", bloodAPlus.getText().toString());
                                    updatedFields.put("bloodTypeBPlus", bloodBPlus.getText().toString());
                                    updatedFields.put("bloodTypeABPlus", bloodABPlus.getText().toString());
                                    updatedFields.put("bloodTypeOPlus", bloodOPlus.getText().toString());
                                    updatedFields.put("bloodTypeBMinus", bloodBMinus.getText().toString());
                                    updatedFields.put("bloodTypeABMinus", bloodABMinus.getText().toString());
                                    updatedFields.put("bloodTypeAMinus", bloodAMinus.getText().toString());
                                    updatedFields.put("bloodTypeOMinus", bloodOMinus.getText().toString());
                                    updatedFields.put("eventMission", eventMission.getText().toString());
                                    if (bloodTypes != null) updatedFields.put("bloodTypes", bloodTypes);

                                    db.collection("events")
                                            .document(document.getId())
                                            .update(updatedFields)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getApplicationContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                                                storeNotification(db, eventDetail.getEventID());
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getApplicationContext(), "Error updating user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Event document not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Error fetching event document: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Error fetching event document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });


            }
        });
    }
    private void storeNotification(FirebaseFirestore db, String eventID) {
        db.collection("registers")
                .whereEqualTo("eventID", eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");

                            if (username != null) {
                                Map<String, Object> notificationData = new HashMap<>();
                                notificationData.put("content", "Event Detail of your Registration: " + eventID + " has been Updated!");
                                notificationData.put("eventID", eventID);
                                notificationData.put("username", username);
                                notificationData.put("time", Timestamp.now());

                                db.collection("notifications")
                                        .add(notificationData)
                                        .addOnSuccessListener(documentReference -> {
                                            Log.d("NOTIFICATION", "Notification stored for user: " + username + " with ID: " + documentReference.getId());
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("NOTIFICATION", "Error adding notification for user: " + username, e);
                                        });
                            }
                        }
                    } else {
                        Log.w("REGISTERS_ERROR", "Error fetching registers: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REGISTERS_ERROR", "Error fetching registers: ", e);
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