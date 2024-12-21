package com.example.asm2_android.View.General;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.asm2_android.Model.GenderEnum;
import com.example.asm2_android.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView dateOfBirth, bloodType,gender;
    private EditText name, email, address, phone;
    private Button updateButton;
    private FloatingActionButton editAvatarButton;
    private ShapeableImageView avatar;
    private ActivityResultLauncher<Intent> bloodTypeLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private Boolean updateFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        backButton = findViewById(R.id.ic_back);
        dateOfBirth = findViewById(R.id.edit_birthday);
        bloodType = findViewById(R.id.edit_blood_type);
        name = findViewById(R.id.edit_name);
        email = findViewById(R.id.edit_email);
        address = findViewById(R.id.edit_address);
        phone = findViewById(R.id.edit_phone);
        gender = findViewById(R.id.edit_gender);
        updateButton = findViewById(R.id.update_button);
        editAvatarButton = findViewById(R.id.edit_avatar_button);
        avatar = findViewById(R.id.avatar);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", null);

        if (currentUser != null) {
            fetchUserDetails(currentUser);
        }

        editAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageLauncher.launch("image/*");
            }
        });

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this,
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

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    uploadImageToFirestore(result,currentUser);
                }
            }
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
            Intent intent = new Intent(EditProfileActivity.this, BloodSelectionActivity.class);
            bloodTypeLauncher.launch(intent);
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updateFlag", updateFlag);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFlag = true;
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .whereEqualTo("username", currentUser)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                Map<String, Object> updatedFields = new HashMap<>();
                                updatedFields.put("email", email.getText().toString());
                                updatedFields.put("phoneNumber", phone.getText().toString());
                                updatedFields.put("address", address.getText().toString());
                                updatedFields.put("birthday", dateOfBirth.getText().toString());
                                updatedFields.put("bloodType", bloodType.getText().toString());
                                updatedFields.put("name", name.getText().toString());
                                updatedFields.put("gender", gender.getText().toString());

                                db.collection("users")
                                        .document(document.getId())
                                        .update(updatedFields)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getApplicationContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getApplicationContext(), "Error updating user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Error fetching user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
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
                        email.setText(currentEmail);
                        address.setText(currentAddress);
                        phone.setText(currentPhone);
                        gender.setText(currentGender);
                        dateOfBirth.setText(currentDOB);
                        bloodType.setText(currentBloodType);
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

    private void uploadImageToFirestore(Uri imageUri, String currentUser) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("Account Avatar - " + currentUser);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userRef = db.collection("users").document(currentUser);

                        userRef.update("profileImage", downloadUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Glide.with(getApplicationContext())
                                            .load(downloadUrl)
                                            .circleCrop()
                                            .into(avatar);
                                    updateFlag = true;
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Avatar updated", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Failed to update avatar", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
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