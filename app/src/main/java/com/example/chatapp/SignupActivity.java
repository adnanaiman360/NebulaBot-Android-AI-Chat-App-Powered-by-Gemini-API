package com.example.chatapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    Button btnSignup;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);

        db = openOrCreateDatabase("UserDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(email VARCHAR, password VARCHAR);");

        btnSignup.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            db.execSQL("INSERT INTO users VALUES(?, ?)", new Object[]{email, password});
            Toast.makeText(SignupActivity.this, "User registered", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}
