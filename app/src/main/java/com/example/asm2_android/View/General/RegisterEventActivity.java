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
import com.example.asm2_android.Model.GenderEnum;
import com.example.asm2_android.Model.RegisterEventClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.Donor.DonorHistoryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class RegisterEventActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView dateOfBirth, bloodType,gender;
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
        weight = findViewById(R.id.register_weight);
        submitButton = findViewById(R.id.submit_button);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", null);

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
                                String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                dateOfBirth.setText(selectedDate);
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
                }

                String dob = dateOfBirth.getText().toString().trim();
                if (dob.isEmpty()) {
                    dateOfBirth.setError("Date of Birth is required");
                    dateOfBirth.requestFocus();
                } else {
                    int age = calculateAge(dob);
                    if (age < 18 || age > 65) {
                        ageFlag = false;
                    }
                }

                String selectedGender = gender.getText().toString().trim();
                if (selectedGender.isEmpty()) {
                    gender.setError("Gender is required");
                    gender.requestFocus();
                }

                if (bloodType.getText().toString().trim().isEmpty()) {
                    bloodType.setError("Blood type is required");
                    bloodType.requestFocus();
                }

                String weightStr = weight.getText().toString().trim();
                if (weightStr.isEmpty()) {
                    weight.setError("Weight is required");
                    weight.requestFocus();
                } else {
                    double weightD = Double.parseDouble(weightStr);
                    if (selectedGender.equalsIgnoreCase("Female") && weightD < 42) {
                        weightFlag = false;
                    } else if (selectedGender.equalsIgnoreCase("Male") && weightD < 45) {
                        weightFlag = false;
                    }
                }

                if (!ageFlag || !weightFlag) {
                    Intent failedIntent = new Intent(RegisterEventActivity.this, FailedRegisterActivity.class);
                    failedIntent.putExtra("AGE_FLAG", ageFlag);
                    failedIntent.putExtra("WEIGHT_FLAG", weightFlag);
                    startActivity(failedIntent);
                    finish();
                } else  {
                    String userName = name.getText().toString().trim();
                    String userAddress = address.getText().toString().trim();
                    String userPhone = phone.getText().toString().trim();
                    String userDOB = dateOfBirth.getText().toString().trim();
                    String userGender = gender.getText().toString().trim();
                    String userBloodType = bloodType.getText().toString().trim();
                    String userWeight = weight.getText().toString().trim();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("registers").document(currentUser)
                            .set(new RegisterEventClass(userName, userAddress, userPhone, userDOB, userGender, userBloodType, userWeight))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent donationIntent = new Intent(RegisterEventActivity.this, DonorHistoryActivity.class);
                                    startActivity(donationIntent);
                                    finish();
                                } else {
                                    Log.d("RegisterEventActivity", "Error: " + task.getException());
                                    Toast.makeText(RegisterEventActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private int calculateAge(String dob) {
        String[] dateParts = dob.split("/");
        int birthDay = Integer.parseInt(dateParts[0]);
        int birthMonth = Integer.parseInt(dateParts[1]);
        int birthYear = Integer.parseInt(dateParts[2]);

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