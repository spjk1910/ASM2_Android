package com.example.asm2_android.View.Donor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.asm2_android.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class DonorHomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView filter, closeFilter;
    private Dialog filterSheet;
    private TextView dateOfEvent;
    private GoogleMap mMap;

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
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_donation);

        String currentUser = getIntent().getStringExtra("CURRENT_USER");

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

        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterSheet.dismiss();
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterSheet.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        String currentDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
        dateOfEvent.setText(currentDate);

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
                                String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                dateOfEvent.setText(selectedDate);
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
                            Boolean open = document.getBoolean("open");

                            if (open) {
                                LatLng eventLocation = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(eventLocation).title(eventName)
                                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.maker_image))));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
                            }
                        }
                    } else {
                        Log.e("Firestore Error", "Error getting documents: ", task.getException());
                    }
                });
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