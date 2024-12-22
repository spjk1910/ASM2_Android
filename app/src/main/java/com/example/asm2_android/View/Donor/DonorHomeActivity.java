package com.example.asm2_android.View.Donor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.asm2_android.Model.CustomInfoMarkerClass;
import com.example.asm2_android.Model.CustomInfoWindowAdapter;
import com.example.asm2_android.R;
import com.example.asm2_android.View.SiteManager.SiteManagerAddEventActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DonorHomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView filter, closeFilter;
    private Button saveButton;
    private Dialog filterSheet;
    private TextView dateOfEvent,clearDate;
    private EditText search;
    private GoogleMap mMap;
    private String startDate;
    private List<String> bloodTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        filter = findViewById(R.id.filter);
        search = findViewById(R.id.search);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_donation);

        String currentUser = getIntent().getStringExtra("CURRENT_USER");

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();
                filterMarkersBySearching(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim().toLowerCase();
                filterMarkersBySearching(query);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_donation) {
                return true;
            } else if (itemId == R.id.menu_history) {
                Intent intent = new Intent(getApplicationContext(), DonorHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            } else if (itemId == R.id.menu_notification) {
                Intent intent = new Intent(getApplicationContext(), DonorNotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            } else if (itemId == R.id.menu_profile) {
                Intent intent = new Intent(getApplicationContext(), DonorProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            }
            return false;
        });

        filterSheet = new Dialog(this);
        filterSheet.setContentView(R.layout.filter_sheet);
        filterSheet.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        filterSheet.getWindow().setBackgroundDrawable(getDrawable(R.drawable.filter_sheet_bg));
        filterSheet.setCancelable(false);

        dateOfEvent = filterSheet.findViewById(R.id.date_of_event);
        closeFilter = filterSheet.findViewById(R.id.close_filter);
        saveButton = filterSheet.findViewById(R.id.buttonSave);
        clearDate = filterSheet.findViewById(R.id.clear_date);


        clearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOfEvent.setText("Select Date");
                startDate = "Select Date";
            }
        });

        closeFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterSheet.dismiss();
                }
            });

        saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isAPlusChecked = ((CheckBox) filterSheet.findViewById(R.id.checkbox_A_plus)).isChecked();
                    boolean isAMinusChecked = ((CheckBox) filterSheet.findViewById(R.id.checkbox_A_minus)).isChecked();
                    boolean isBPlusChecked = ((CheckBox) filterSheet.findViewById(R.id.checkbox_B_plus)).isChecked();
                    boolean isBMinusChecked = ((CheckBox) filterSheet.findViewById(R.id.checkbox_B_minus)).isChecked();
                    boolean isOPlusChecked = ((CheckBox) filterSheet.findViewById(R.id.checkbox_O_plus)).isChecked();
                    boolean isOMinusChecked = ((CheckBox) filterSheet.findViewById(R.id.checkbox_O_minus)).isChecked();
                    boolean isABPlusChecked = ((CheckBox) filterSheet.findViewById(R.id.checkbox_AB_plus)).isChecked();
                    boolean isABMinusChecked = ((CheckBox) filterSheet.findViewById(R.id.checkbox_AB_minus)).isChecked();

                    startDate = dateOfEvent.getText().toString();

                    bloodTypes = new ArrayList<>();
                    if (isAPlusChecked) bloodTypes.add("A+");
                    if (isAMinusChecked) bloodTypes.add("A-");
                    if (isBPlusChecked) bloodTypes.add("B+");
                    if (isBMinusChecked) bloodTypes.add("B-");
                    if (isOPlusChecked) bloodTypes.add("O+");
                    if (isOMinusChecked) bloodTypes.add("O-");
                    if (isABPlusChecked) bloodTypes.add("AB+");
                    if (isABMinusChecked) bloodTypes.add("AB-");

                    filterSheet.dismiss();
                    mMap.clear();
                    filterMarkersByFiltering();
                }
            });

        filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterSheet.show();
                }
            });

        dateOfEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(filterSheet.getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                    Calendar selectedCalendar = Calendar.getInstance();
                                    selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);

                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd.yyyy", Locale.getDefault());
                                    String formattedDate = sdf.format(selectedCalendar.getTime());

                                    dateOfEvent.setText(formattedDate);
                                }
                            },
                            year, month, dayOfMonth);
                    datePickerDialog.show();
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

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("events")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                double latitude = document.getDouble("latitude");
                                double longitude = document.getDouble("longitude");
                                String eventName = document.getString("eventName");
                                List<String> bloodTypeList = (List<String>) document.get("bloodTypes");
                                String eventDate = document.getString("dateStart");
                                Boolean open = document.getBoolean("open");

                                String bloodTypes = String.join(", ", bloodTypeList);
                                bloodTypes = "Blood Types Needed: " + bloodTypes;
                                eventDate = "Start Date: " + eventDate;

                                if (open) {
                                    LatLng eventLocation = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(eventLocation)
                                            .title(eventName)
                                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.marker_image)));

                                    Marker marker = mMap.addMarker(markerOptions);
                                    if (marker != null) {
                                        marker.setTag(new CustomInfoMarkerClass(eventName, bloodTypes, eventDate, R.drawable.marker_info_image));
                                    }
                                    if(mMap != null) {
                                        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(DonorHomeActivity.this));
                                    }
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
                                }
                            }
                        } else {
                            Log.e("Firestore Error", "Error getting documents: ", task.getException());
                        }
                    });
        }

        private void filterMarkersBySearching(String query) {
            mMap.clear();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String eventName = document.getString("eventName");
                                if (eventName != null && eventName.toLowerCase().contains(query)) {
                                    double latitude = document.getDouble("latitude");
                                    double longitude = document.getDouble("longitude");
                                    LatLng eventLocation = new LatLng(latitude, longitude);

                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(eventLocation)
                                            .title(eventName)
                                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.marker_image)));

                                    Marker marker = mMap.addMarker(markerOptions);
                                    if (marker != null) {
                                        marker.setTag(new CustomInfoMarkerClass(
                                                eventName,
                                                "Blood Types Needed: " + String.join(", ", (List<String>) document.get("bloodTypes")),
                                                "Start Date: " + document.getString("dateStart"),
                                                R.drawable.marker_info_image
                                        ));
                                    }
                                }
                            }
                        } else {
                            Log.e("Firestore Error", "Error getting documents: ", task.getException());
                        }
                    });
        }


        private void filterMarkersByFiltering() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("events")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                double latitude = document.getDouble("latitude");
                                double longitude = document.getDouble("longitude");
                                String eventName = document.getString("eventName");
                                List<String> eventBloodTypes = (List<String>) document.get("bloodTypes");
                                String eventDate = document.getString("dateStart");
                                Boolean open = document.getBoolean("open");

                                if (open){
                                    if ((!(startDate.equals("Select Date")) && !checkDate(eventDate)) ||
                                            !bloodTypes.isEmpty() && eventBloodTypes.stream().noneMatch(bloodTypes::contains)) {
                                        continue;
                                    }
                                    LatLng eventLocation = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(eventLocation)
                                            .title(eventName)
                                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.marker_image)));

                                    Marker marker = mMap.addMarker(markerOptions);
                                    if (marker != null) {
                                        marker.setTag(new CustomInfoMarkerClass(eventName, String.join(", ", eventBloodTypes), "Start Date: " + eventDate, R.drawable.marker_info_image));
                                    }
                                    if (mMap != null) {
                                        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(DonorHomeActivity.this));
                                    }
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
                                }
                            }
                        } else {
                            Log.e("Firestore Error", "Error getting documents: ", task.getException());
                        }
                    });
        }

        private boolean checkDate(String eventDate) {
            if (startDate.equals("Select Date")) return true;
            String[] event = eventDate.split("\\.");
            String[] start = startDate.split("\\.");
            String eventMonthStr = event[0];
            String startMonthStr = start[0];

            int eventDay = Integer.parseInt(event[1]);
            int eventYear = Integer.parseInt(event[2]);
            int startDay = Integer.parseInt(start[1]);
            int startYear = Integer.parseInt(start[2]);
            int eventMonth = getMonthNumber(eventMonthStr);
            int startMonth = getMonthNumber(startMonthStr);

            Log.d("DATE_PARSE","Event Year"+eventYear + "Start Year" + startYear);
            Log.d("DATE_PARSE","Event Month"+eventMonth + "Start Month" + startMonth);
            Log.d("DATE_PARSE","Event Day"+eventDay + "Start Day" + startDay);

            if (startYear > eventYear) {
                return true;
            } else if (startYear < eventYear) {
                return false;
            }

            if (startMonth > eventMonth) {
                return true;
            } else if (startMonth < eventMonth) {
                return false;
            }

            return startDay >= eventDay;
        }

        private int getMonthNumber(String monthName) {
            switch (monthName) {
                case "Jan": return 1;
                case "Feb": return 2;
                case "Mar": return 3;
                case "Apr": return 4;
                case "May": return 5;
                case "Jun": return 6;
                case "Jul": return 7;
                case "Aug": return 8;
                case "Sep": return 9;
                case "Oct": return 10;
                case "Nov": return 11;
                case "Dec": return 12;
                default: return -1;
            }
        }


        private Bitmap getBitmapFromDrawable(int vectorResId) {
            Bitmap bitmap = null;
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), vectorResId, null);

            if (drawable != null) {
                bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }
            return bitmap;
        }

    }