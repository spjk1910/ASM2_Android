package com.example.asm2_android.View;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2_android.R;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private EditText loginUsername, loginPassword;
    private ImageView passwordVisibility;
    private Button signInButton;
    private TextView redirectSignUp;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordVisibility = findViewById(R.id.password_visibility);
        signInButton = findViewById(R.id.sign_in_button);
        redirectSignUp = findViewById(R.id.redirect_sign_up);
        loginUsername = findViewById(R.id.register_username);
        loginPassword = findViewById(R.id.register_password);

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
                                // Successfully logged in
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, DonorHomeActivity.class);
                                startActivity(intent);
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

                                                if (dbPassword2 != null && dbPassword2.equals(password)) {
                                                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(LoginActivity.this, DonorHomeActivity.class);
                                                    startActivity(intent);
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
}