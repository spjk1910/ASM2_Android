package com.example.asm2_android.View.General;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2_android.Model.UserClass;
import com.example.asm2_android.Model.UserRoleEnum;
import com.example.asm2_android.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private CollectionReference usersCollection;
    private EditText registerName,registerEmail, registerPassword,registerUsername;
    private ImageView passwordVisibility;
    private Button signUpButton;
    private TextView redirectSignIn;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        firestore = FirebaseFirestore.getInstance();
        usersCollection = firestore.collection("users");
        passwordVisibility = findViewById(R.id.password_visibility);
        signUpButton = findViewById(R.id.sign_up_button);
        redirectSignIn = findViewById(R.id.redirect_sign_in);
        registerName = findViewById(R.id.register_name);
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerUsername = findViewById(R.id.register_username);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = registerName.getText().toString();
                String email = registerEmail.getText().toString();
                String username = registerUsername.getText().toString();
                String password = registerPassword.getText().toString();

                if (name.isEmpty()) {
                    registerName.setError("Name is required");
                    registerName.requestFocus();
                } else if (email.isEmpty() || !isValidEmail(email)) {
                    registerEmail.setError(email.isEmpty() ? "Email is required" : "Invalid email format");
                    registerEmail.requestFocus();
                } else if (username.isEmpty() || username.contains(" ")) {
                    registerUsername.setError(username.isEmpty() ? "Username is required" : "Username cannot contain spaces");
                    registerUsername.requestFocus();
                } else if (password.isEmpty() || password.contains(" ")) {
                    registerPassword.setError(password.isEmpty() ? "Password is required" : "Password cannot contain spaces");
                    registerPassword.requestFocus();
                } else {
                   checkUsernameExist(username,email);
                }
            }
        });

        passwordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordVisibility.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordVisibility.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;

                registerPassword.setSelection(registerPassword.getText().length());
            }
        });

        redirectSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void checkUsernameExist(final String username, final String email) {
        usersCollection.whereEqualTo("username", username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Username already exists. Please choose another!", Toast.LENGTH_SHORT).show();
                        } else {
                            checkEmailExists(username, email);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error checking username. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkEmailExists(final String username, final String email) {
        usersCollection.whereEqualTo("email", email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Email is already registered. Please choose another!", Toast.LENGTH_SHORT).show();
                        } else {
                            registerUser(username, email);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error checking email. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser(String username, String email) {
        String name = registerName.getText().toString();
        String password = registerPassword.getText().toString();

        UserClass newUser = new UserClass(name, email, username, password, "", "", null, UserRoleEnum.DONORS, "");

        showLoadingScreen();

        usersCollection.document(username).set(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show();
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

    private void showLoadingScreen() {
        Intent intent = new Intent(RegisterActivity.this, LoadingScreenActivity.class);
        intent.putExtra("ACTIVITY_NAME", LoginActivity.class.getName());
        startActivity(intent);
    }
}