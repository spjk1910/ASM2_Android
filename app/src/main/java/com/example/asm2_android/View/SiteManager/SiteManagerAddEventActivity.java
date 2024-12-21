package com.example.asm2_android.View.SiteManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2_android.Model.EventClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.ChooseLocationActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SiteManagerAddEventActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView location, dateStart,siteManagerName;
    private Button submitButton;
    private EditText eventName, contact, eventMission;
    private ActivityResultLauncher<Intent> chooseLocationLauncher;
    private ProgressBar progressBar;
    private CheckBox A_plus, A_minus, B_plus, B_minus, O_plus, O_minus, AB_plus, AB_minus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_manager_add_event);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.ic_back);
        location = findViewById(R.id.add_location);
        dateStart = findViewById(R.id.add_date_start);
        eventName = findViewById(R.id.add_event_name);
        siteManagerName = findViewById(R.id.add_site_manager_name);
        contact = findViewById(R.id.add_contact);
        eventMission = findViewById(R.id.add_event_mission);
        submitButton = findViewById(R.id.submit_button);
        A_plus = findViewById(R.id.checkbox_A_plus);
        A_minus = findViewById(R.id.checkbox_A_minus);
        B_plus = findViewById(R.id.checkbox_B_plus);
        B_minus = findViewById(R.id.checkbox_B_minus);
        O_plus = findViewById(R.id.checkbox_O_plus);
        O_minus = findViewById(R.id.checkbox_O_minus);
        AB_plus = findViewById(R.id.checkbox_AB_plus);
        AB_minus = findViewById(R.id.checkbox_AB_minus);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", null);
        if (currentUser != null) {
            fetchUserDetails(currentUser);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventName.setError(null);
                siteManagerName.setError(null);
                contact.setError(null);
                location.setError(null);
                dateStart.setError(null);
                eventMission.setError(null);
                A_plus.setError(null);
                A_minus.setError(null);
                B_plus.setError(null);
                B_minus.setError(null);
                O_plus.setError(null);
                O_minus.setError(null);
                AB_plus.setError(null);
                AB_minus.setError(null);

                if (eventName.getText().toString().isEmpty()) {
                    eventName.setError("Event Name is required");
                    eventName.requestFocus();
                }else if (siteManagerName.getText().toString().isEmpty()) {
                    siteManagerName.setError("Site Manager Name is required");
                    siteManagerName.requestFocus();
                }else if (contact.getText().toString().isEmpty()) {
                    contact.setError("Contact is required");
                    contact.requestFocus();
                }else if (location.getText().toString().isEmpty()) {
                    location.setError("Location is required");
                    location.requestFocus();
                }else if (dateStart.getText().toString().isEmpty()) {
                    dateStart.setError("Start Date is required");
                    dateStart.requestFocus();
                }else if (eventMission.getText().toString().isEmpty()) {
                    eventMission.setError("Event Mission is required");
                    eventMission.requestFocus();
                }else if (getSelectedBloodTypes().length == 0) {
                    Toast.makeText(SiteManagerAddEventActivity.this, "At least one blood type must be selected!", Toast.LENGTH_SHORT).show();
                } else {
                    String[] bloodTypes = getSelectedBloodTypes();

                    EventClass event = new EventClass(
                            eventName.getText().toString(),
                            siteManagerName.getText().toString(),
                            contact.getText().toString(),
                            location.getText().toString(),
                            dateStart.getText().toString(),
                            eventMission.getText().toString(),
                            bloodTypes
                    );

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("eventName", event.getEventName());
                    eventData.put("siteManagerName", event.getSiteManagerName());
                    eventData.put("contact", event.getContact());
                    eventData.put("location", event.getLocation());
                    eventData.put("dateStart", event.getDateStart());
                    eventData.put("eventMission", event.getEventMission());
                    eventData.put("bloodTypes", Arrays.asList(bloodTypes));

                    db.collection("users")
                            .document(currentUser)
                            .collection("events")
                            .add(eventData)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("Firestore", "Event added with ID: " + documentReference.getId());
                                Toast.makeText(getApplicationContext(), "Blood Donation Event created successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firestore", "Error adding event", e);
                                Toast.makeText(getApplicationContext(), "Error creating Blood Donation Event!", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });



        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SiteManagerAddEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);

                                SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd.yyyy", Locale.getDefault());
                                String formattedDate = sdf.format(selectedCalendar.getTime());

                                dateStart.setText(formattedDate);
                            }
                        },
                        year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Intent intent = new Intent(SiteManagerAddEventActivity.this, ChooseLocationActivity.class);
                chooseLocationLauncher.launch(intent);
            }
        });

        chooseLocationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    progressBar.setVisibility(View.GONE);

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            double latitude = data.getDoubleExtra("latitude", 0.0);
                            double longitude = data.getDoubleExtra("longitude", 0.0);

                            LatLng selectedLocation = new LatLng(latitude, longitude);
                            location.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                        }
                    }
                }
        );

    }

    private void fetchUserDetails(String currentUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(currentUser)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        siteManagerName.setText(name);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting document", e);
                    Toast.makeText(SiteManagerAddEventActivity.this, "Error fetching user details!", Toast.LENGTH_SHORT).show();
                });
    }

    private String[] getSelectedBloodTypes() {
        List<String> selectedBloodTypes = new ArrayList<>();

        if (A_plus.isChecked()) selectedBloodTypes.add("A+");
        if (A_minus.isChecked()) selectedBloodTypes.add("A-");
        if (B_plus.isChecked()) selectedBloodTypes.add("B+");
        if (B_minus.isChecked()) selectedBloodTypes.add("B-");
        if (O_plus.isChecked()) selectedBloodTypes.add("O+");
        if (O_minus.isChecked()) selectedBloodTypes.add("O-");
        if (AB_plus.isChecked()) selectedBloodTypes.add("AB+");
        if (AB_minus.isChecked()) selectedBloodTypes.add("AB-");

        return selectedBloodTypes.toArray(new String[0]);
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