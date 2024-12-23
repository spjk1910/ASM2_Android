package com.example.asm2_android.View.General;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.asm2_android.Model.BloodVolumeEnum;
import com.example.asm2_android.Model.GenderEnum;
import com.example.asm2_android.Model.RegisterEventClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.Donor.DonorHistoryActivity;
import com.example.asm2_android.View.Donor.DonorHomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RegisterEventActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView dateOfBirth, bloodType,gender,bloodVolume;
    private EditText name, address, phone,weight;
    private Button submitButton;
    private ActivityResultLauncher<Intent> bloodTypeLauncher;
    private Boolean ageFlag = true,weightFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        backButton = findViewById(R.id.ic_back);
        dateOfBirth = findViewById(R.id.register_birthday);
        bloodType = findViewById(R.id.register_blood_type);
        name = findViewById(R.id.register_name);
        address = findViewById(R.id.register_address);
        phone = findViewById(R.id.register_phone);
        gender = findViewById(R.id.edit_gender);
        bloodVolume = findViewById(R.id.edit_volume_blood);
        weight = findViewById(R.id.register_weight);
        submitButton = findViewById(R.id.submit_button);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", null);
        String eventID = getIntent().getStringExtra("EVENT_ID");
        List<String> eventBloodTypes = getIntent().getStringArrayListExtra("EVENT_BLOOD_TYPES");

        if (currentUser != null) {
            fetchUserDetails(currentUser);
        }

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);

                                SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd.yyyy", Locale.getDefault());
                                String formattedDate = sdf.format(selectedCalendar.getTime());

                                dateOfBirth.setText(formattedDate);
                            }
                        },
                        year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        String[] genderOptions = Arrays.stream(GenderEnum.values())
                .map(Enum::name)
                .toArray(String[]::new);

        gender.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, gender);

            for (String gender : genderOptions) {
                popupMenu.getMenu().add(gender);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                gender.setText(item.getTitle());
                return true;
            });

            popupMenu.show();
        });

        int[] bloodVolumeValues = Arrays.stream(BloodVolumeEnum.values())
                .mapToInt(BloodVolumeEnum::getVolume)
                .toArray();

        bloodVolume.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, bloodVolume);

            for (int volume : bloodVolumeValues) {
                popupMenu.getMenu().add(String.valueOf(volume) + " ml");
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                bloodVolume.setText(item.getTitle());
                return true;
            });

            popupMenu.show();
        });

        bloodTypeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String selectedBloodType = data.getStringExtra("selectedBloodType");
                            bloodType.setText(selectedBloodType);
                        }
                    }
                });

        bloodType.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterEventActivity.this, BloodSelectionActivity.class);
            bloodTypeLauncher.launch(intent);
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Name is required");
                    name.requestFocus();
                }else if (address.getText().toString().trim().isEmpty()) {
                    address.setError("Address is required");
                    address.requestFocus();
                }else if (phone.getText().toString().trim().isEmpty()) {
                    phone.setError("Phone number is required");
                    phone.requestFocus();
                }else if (dateOfBirth.getText().toString().trim().isEmpty()) {
                    dateOfBirth.setError("Date of Birth is required");
                    dateOfBirth.requestFocus();
                }else if (gender.getText().toString().trim().equals(GenderEnum.GENDER.toString())) {
                    gender.setError("Gender is required");
                    gender.requestFocus();
                }else if (bloodType.getText().toString().trim().isEmpty()) {
                    bloodType.setError("Blood type is required");
                    bloodType.requestFocus();
                }else if (weight.getText().toString().trim().isEmpty()) {
                    weight.setError("Weight is required");
                    weight.requestFocus();
                }else if (bloodVolume.getText().toString().trim().equals(String.valueOf(BloodVolumeEnum.VOLUME.getVolume()) + " ml") || bloodVolume.getText().toString().trim().isEmpty()) {
                    bloodVolume.setError("Blood volume is required");
                    bloodVolume.requestFocus();
            } else {
                    boolean isValid = true;
                    int age = calculateAge(dateOfBirth.getText().toString());
                    String selectedGender = gender.getText().toString().trim();
                    String weightStr = weight.getText().toString().trim().replaceAll(",", ".");
                    double selectedWeight = Double.parseDouble(weightStr);
                    String selectedBloodType = bloodType.getText().toString().trim();

                    if (!eventBloodTypes.contains(selectedBloodType)) {
                        Toast.makeText(RegisterEventActivity.this,"Thank you, but this event doesn’t need the blood type you selected!",Toast.LENGTH_SHORT).show();
                        Intent failedIntent = new Intent(RegisterEventActivity.this, DonorHomeActivity.class);
                        startActivity(failedIntent);
                        finish();
                        return;
                    }

                    if (age < 18 || age > 65) {
                        isValid = false;
                        ageFlag = false;
                    }
                    if ((selectedGender.equals("Female") && selectedWeight < 42) ||
                            (selectedGender.equals("Male") && selectedWeight < 45)) {
                        isValid = false;
                        weightFlag = false;
                    }

                    if (isValid) {
                        String userName = name.getText().toString().trim();
                        String userAddress = address.getText().toString().trim();
                        String userPhone = phone.getText().toString().trim();
                        String userDOB = dateOfBirth.getText().toString().trim();
                        String userGender = gender.getText().toString().trim();
                        String userBloodType = bloodType.getText().toString().trim();
                        String userWeight = weight.getText().toString().trim();
                        String userBloodVolume = bloodVolume.getText().toString().trim();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("registers").document(currentUser+"|"+eventID)
                                .set(new RegisterEventClass(currentUser,userName,userAddress,userPhone, userDOB, userGender, userBloodType, userWeight,eventID,userBloodVolume))
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterEventActivity.this, "Thank you for your registration in event!", Toast.LENGTH_SHORT).show();
                                        Intent donationIntent = new Intent(RegisterEventActivity.this, DonorHistoryActivity.class);
                                        startActivity(donationIntent);
                                        finish();
                                    } else {
                                        Log.d("RegisterEventActivity", "Error: " + task.getException());
                                        Toast.makeText(RegisterEventActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Intent failedIntent = new Intent(RegisterEventActivity.this, FailedRegisterActivity.class);
                        failedIntent.putExtra("AGE_FLAG", ageFlag);
                        failedIntent.putExtra("WEIGHT_FLAG", weightFlag);
                        startActivity(failedIntent);
                        finish();
                    }
                }
            }
        });
    }

    private int calculateAge(String dob) {
        String[] birthday = dob.split("\\.");
        String birthMonthStr = birthday[0];

        int birthDay = Integer.parseInt(birthday[1]);
        int birthYear = Integer.parseInt(birthday[2]);
        int birthMonth = getMonthNumber(birthMonthStr);

        Calendar birthDate = Calendar.getInstance();
        birthDate.set(birthYear, birthMonth - 1, birthDay);

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
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

    private void fetchUserDetails(String currentUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", currentUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String currentName = document.getString("name");
                        String currentAddress = document.getString("address");
                        String currentPhone = document.getString("phoneNumber");
                        String currentDOB = document.getString("birthday");
                        String currentBloodType = document.getString("bloodType");
                        String currentGender = document.getString("gender");

                        name.setText(currentName);
                        address.setText(currentAddress);
                        phone.setText(currentPhone);
                        gender.setText(currentGender);
                        bloodType.setText(currentBloodType);
                        dateOfBirth.setText(currentDOB);
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