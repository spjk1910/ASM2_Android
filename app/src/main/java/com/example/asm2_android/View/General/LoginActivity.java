package com.example.asm2_android.View.General;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2_android.Model.UserRoleEnum;
import com.example.asm2_android.R;

import com.example.asm2_android.View.Donor.DonorHomeActivity;
import com.example.asm2_android.View.SiteManager.SiteManagerHomeActivity;
import com.example.asm2_android.View.SuperUser.SuperUserHomeActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText loginUsername, loginPassword;
    private ImageView passwordVisibility, closeAboutUs, aboutUsButton;
    private Button signInButton;
    private TextView redirectSignUp, aboutUsEmail;
    private Dialog aboutUs;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        passwordVisibility = findViewById(R.id.password_visibility);
        signInButton = findViewById(R.id.sign_in_button);
        redirectSignUp = findViewById(R.id.redirect_sign_up);
        loginUsername = findViewById(R.id.register_username);
        loginPassword = findViewById(R.id.register_password);
        aboutUsButton = findViewById(R.id.about_us_button);

        aboutUs = new Dialog(this);
        aboutUs.setContentView(R.layout.about_us_sheet);
        aboutUs.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        aboutUs.getWindow().setBackgroundDrawable(getDrawable(R.drawable.about_us_sheet_bg));
        aboutUs.setCancelable(false);
        aboutUsEmail = aboutUs.findViewById(R.id.email);


        closeAboutUs = aboutUs.findViewById(R.id.close_filter);

        closeAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutUs.dismiss();
            }
        });

        aboutUsEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:s4027648@rmit.edu.vn"));

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else {
                    try {
                        Toast.makeText(v.getContext(), "You don't have an app, Please installed!", Toast.LENGTH_SHORT).show();
                        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm"));
                        startActivity(playStoreIntent);
                    } catch (Exception e) {
                        Toast.makeText(v.getContext(), "Unable to open Google Play Store", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutUs.show();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {
                    return;
                } else {
                    signIn();
                }
            }
        });

        redirectSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        passwordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordVisibility.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordVisibility.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;

                loginPassword.setSelection(loginPassword.getText().length());
            }
        });
    }

    public Boolean validateUsername() {
        String check = loginUsername.getText().toString();
        if (check.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String check = loginPassword.getText().toString();
        if (check.isEmpty()) {
            loginUsername.setError("Password cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public void signIn() {
        String username = loginUsername.getText().toString();
        String password = loginPassword.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String dbPassword = document.getString("password");

                            if (dbPassword != null && dbPassword.equals(password)) {
                                String roleStr = document.getString("role");
                                UserRoleEnum role = null;
                                if (UserRoleEnum.DONORS.name().equals(roleStr)) {
                                    role = UserRoleEnum.DONORS;
                                } else if (UserRoleEnum.SITE_MANAGERS.name().equals(roleStr)) {
                                    role = UserRoleEnum.SITE_MANAGERS;
                                } else if (UserRoleEnum.SUPER_USERS.name().equals(roleStr)) {
                                    role = UserRoleEnum.SUPER_USERS;
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid role!", Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                if (role != null) {
                                    showLoadingScreen(getHomeActivityClass(role),username,role);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Role not defined for user!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                loginPassword.setError("Incorrect password!");
                                loginPassword.requestFocus();
                            }
                        } else {
                            db.collection("users")
                                    .whereEqualTo("email", username)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            QuerySnapshot querySnapshot2 = task2.getResult();
                                            if (querySnapshot2 != null && !querySnapshot2.isEmpty()) {
                                                DocumentSnapshot document2 = querySnapshot2.getDocuments().get(0);
                                                String dbPassword2 = document2.getString("password");
                                                String dbUsername2 = document2.getString("username");

                                                if (dbPassword2 != null && dbPassword2.equals(password)) {
                                                    String roleStr = document2.getString("role");
                                                    UserRoleEnum role = null;
                                                    if (UserRoleEnum.DONORS.name().equals(roleStr)) {
                                                        role = UserRoleEnum.DONORS;
                                                    } else if (UserRoleEnum.SITE_MANAGERS.name().equals(roleStr)) {
                                                        role = UserRoleEnum.SITE_MANAGERS;
                                                    } else if (UserRoleEnum.SUPER_USERS.name().equals(roleStr)) {
                                                        role = UserRoleEnum.SUPER_USERS;
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "Invalid role!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                                    if (role != null) {
                                                        showLoadingScreen(getHomeActivityClass(role),dbUsername2,role);
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "Role not defined for user!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    loginPassword.setError("Incorrect password!");
                                                    loginPassword.requestFocus();
                                                }
                                            } else {
                                                loginUsername.setError("User does not exist!");
                                                loginUsername.requestFocus();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Database error: " + task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Database error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    private Class<?> getHomeActivityClass(UserRoleEnum role) {
        switch (role) {
            case DONORS:
                return DonorHomeActivity.class;
            case SITE_MANAGERS:
                return SiteManagerHomeActivity.class;
            case SUPER_USERS:
                return SuperUserHomeActivity.class;
            default:
                Toast.makeText(LoginActivity.this, "Invalid role!", Toast.LENGTH_SHORT).show();
                return null;
        }
    }

    private void showLoadingScreen(Class<?> targetActivity, String currentUser, UserRoleEnum currentRole) {
        Intent intent = new Intent(LoginActivity.this, LoadingScreenActivity.class);
        intent.putExtra("ACTIVITY_NAME", targetActivity.getName());
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", currentUser);
        editor.putString("userRole", String.valueOf(currentRole));
        editor.apply();
        Log.d("Current User", currentUser);
        startActivity(intent);
    }
}