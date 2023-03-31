package com.example.gaanbajaw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signinActivity extends AppCompatActivity {

    ProgressDialog dialog;
    FirebaseAuth auth;
    Button signin;
    EditText mail, password;
    BottomNavigationView bottomNavigationView;
    TextView signup1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(signinActivity.this);
        dialog.setTitle("LOGIN");
        dialog.setMessage("LOGIN to your account");
        signin = findViewById(R.id.signin);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        signup1 = findViewById(R.id.signup);

        signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signinActivity.this,signupActivity.class);
                startActivity(intent);

            }
        });


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.onlineMusic);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.onlineMusic :
                        return true;

                    case R.id.deviceMusic :
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);

                        return false;


                }
                return true;
            }
        });



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mail.getText().toString().equals("")) {
                    Toast.makeText(signinActivity.this, "Please enter your G-suit mail", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(signinActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();

                } else {

                    dialog.show();
                    auth.signInWithEmailAndPassword(mail.getText().toString(), password.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    dialog.dismiss();

                                    if (task.isSuccessful()) {

                                        Intent intent = new Intent(signinActivity.this, OnlineSong.class);
                                        startActivity(intent);
                                    } else {

                                        Toast.makeText(signinActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }


            }
        });
        if (auth.getCurrentUser() != null) {

            Intent intent = new Intent(signinActivity.this, OnlineSong.class);
            startActivity(intent);
        }
    }
}