package com.example.asm2_android.View.General;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.asm2_android.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountManagementActivity extends AppCompatActivity {
    private TextView username,email,password,deleteAccount,forgotPassword,forgotPasswordEmail;
    private ImageView backButton, closeButtonEmail,closeButtonPassword;
    private Button emailButton,passwordButton;
    private EditText currentPassword,newPassword,confirmPassword,
            currentEmailPassword,newEmail;
    private ImageView currentPasswordVisibility,newPasswordVisibility,confirmPasswordVisibility,
            currentEmailPasswordVisibility;
    private Boolean isPasswordVisible,isPasswordVisibleConfirm,isPasswordVisibleNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        deleteAccount = findViewById(R.id.delete_account);
        backButton = findViewById(R.id.ic_back);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", null);

        if (currentUser != null) {
            fetchUserDetails(currentUser);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AccountManagementActivity.this)
                        .setMessage("Are you sure you want to delete your account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String username = currentUser;

                                db.collection("users").document(username)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Delete Account", "Error deleting document", e);
                                            }
                                        });
                                Intent intent = new Intent(AccountManagementActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        Dialog emailDialog = new Dialog(AccountManagementActivity.this);
        emailDialog.setContentView(R.layout.change_email_sheet);
        emailDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeButtonEmail = emailDialog.findViewById(R.id.close_button);
        emailButton = emailDialog.findViewById(R.id.confirm_button);
        currentEmailPassword = emailDialog.findViewById(R.id.current_password);
        newEmail = emailDialog.findViewById(R.id.change_email);
        currentEmailPasswordVisibility = emailDialog.findViewById(R.id.current_password_visibility);
        forgotPasswordEmail = emailDialog.findViewById(R.id.forgot_password);

        forgotPasswordEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountManagementActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        currentEmailPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    currentEmailPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    currentEmailPasswordVisibility.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    currentEmailPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    currentEmailPasswordVisibility.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;

                currentEmailPassword.setSelection(currentEmailPassword.getText().length());
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPasswordVisible = false;
                emailDialog.show();
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentEmailPassword.getText().toString().isEmpty()) {
                    currentEmailPassword.setError("It cannot be blank!");
                    currentEmailPassword.requestFocus();
                    return;
                } else if (!isValidEmail(newEmail.getText().toString())) {
                    newEmail.setError("Invalid email format!");
                    newEmail.requestFocus();
                    return;
                } else if (newEmail.getText().toString().isEmpty()) {
                    newEmail.setError("It cannot be blank!");
                    newEmail.requestFocus();
                    return;
                } else if (!currentEmailPassword.getText().toString().equals(password.getText().toString())) {
                    currentEmailPassword.setError("Incorrect password!");
                    currentEmailPassword.requestFocus();
                    return;
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("users")
                            .whereEqualTo("username", currentUser)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    Map<String, Object> updatedFields = new HashMap<>();
                                    updatedFields.put("email", newEmail.getText().toString());

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
                emailDialog.dismiss();
                fetchUserDetails(currentUser);
                recreate();
            }
        });

        closeButtonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailDialog.dismiss();
            }
        });

        Dialog passwordDialog = new Dialog(AccountManagementActivity.this);
        passwordDialog.setContentView(R.layout.change_password_sheet);
        passwordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeButtonPassword = passwordDialog.findViewById(R.id.close_button);
        passwordButton = passwordDialog.findViewById(R.id.confirm_button);
        currentPassword = passwordDialog.findViewById(R.id.current_password);
        newPassword = passwordDialog.findViewById(R.id.change_password);
        confirmPassword = passwordDialog.findViewById(R.id.confirm_password);
        currentPasswordVisibility = passwordDialog.findViewById(R.id.current_password_visibility);
        newPasswordVisibility = passwordDialog.findViewById(R.id.new_password_visibility);
        confirmPasswordVisibility = passwordDialog.findViewById(R.id.confirm_password_visibility);
        forgotPassword = passwordDialog.findViewById(R.id.forgot_password);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountManagementActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        currentPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    currentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    currentPasswordVisibility.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    currentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    currentPasswordVisibility.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;

                currentPassword.setSelection(currentPassword.getText().length());
            }
        });

        newPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisibleNew) {
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newPasswordVisibility.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    newPasswordVisibility.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisibleNew = !isPasswordVisibleNew;

                newPassword.setSelection(newPassword.getText().length());
            }
        });

        confirmPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisibleConfirm) {
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirmPasswordVisibility.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirmPasswordVisibility.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisibleConfirm = !isPasswordVisibleConfirm;

                confirmPassword.setSelection(confirmPassword.getText().length());
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPasswordVisible = false;
                isPasswordVisibleConfirm = false;
                isPasswordVisibleNew = false;
                passwordDialog.show();
            }
        });

        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPassword.getText().toString().isEmpty()) {
                    currentPassword.setError("It cannot be blank!");
                    currentPassword.requestFocus();
                    return;
                } else if (newPassword.getText().toString().isEmpty()) {
                    newPassword.setError("It cannot be blank!");
                    newPassword.requestFocus();
                    return;
                } else if (confirmPassword.getText().toString().isEmpty()){
                    confirmPassword.setError("It cannot be blank!");
                    confirmPassword.requestFocus();
                    return;
                } else if (!currentPassword.getText().toString().equals(password.getText().toString())) {
                    currentPassword.setError("Incorrect password!");
                    currentPassword.requestFocus();
                    return;
                }else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                    confirmPassword.setError("Password does not match!");
                    confirmPassword.requestFocus();
                    return;
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("users")
                            .whereEqualTo("username", currentUser)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    Map<String, Object> updatedFields = new HashMap<>();
                                    updatedFields.put("password", newPassword.getText().toString());

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
                passwordDialog.dismiss();
                fetchUserDetails(currentUser);
                recreate();
            }
        });

        closeButtonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordDialog.dismiss();
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
                        String currentEmail = document.getString("email");
                        String currentPassword = document.getString("password");

                        email.setText(currentEmail);
                        password.setText(currentPassword);
                        username.setText(currentUser);
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
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }
}
