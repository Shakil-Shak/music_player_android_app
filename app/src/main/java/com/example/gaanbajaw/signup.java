package com.example.gaanbajaw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.app.Activity.RESULT_OK;


public class signup<view> extends Fragment {



    public signup() {
        // Required empty public constructor
    }

    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    Button signup,uplode;
    EditText name,email,password;
    TextView songname;
    Uri uri;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User");

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Creating account");
        dialog.setMessage("We are creating your account");

        signup = view.findViewById(R.id.regester);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        name = view.findViewById(R.id.name);
        songname = view.findViewById(R.id.songName);
        uplode = view.findViewById(R.id.FirstUplode);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter Name", Toast.LENGTH_SHORT).show();
                }
                else if (email.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter mail", Toast.LENGTH_SHORT).show();
                }
                else if (password.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter Password", Toast.LENGTH_SHORT).show();
                }
                else if (songname.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please uplode a song", Toast.LENGTH_SHORT).show();
                }
                else {

                    dialog.show();

                    auth.createUserWithEmailAndPassword
                            (email.getText().toString(), password.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    dialog.dismiss();

                                    if (task.isSuccessful()) {

                                        String userName = name.getText().toString();
                                        String userMail = email.getText().toString();


                                        student user = new student(userName,userMail);

                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


                                        //String uid = task.getResult().getUser().getUid();
                                        myRef.child(uid).child("Info").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    Toast.makeText(getContext(), "Regestration Successfully", Toast.LENGTH_SHORT).show();

                                                    name.setText("");
                                                    email.setText("");
                                                    password.setText("");

                                                    Intent intent = new Intent(getContext(), OnlineSong.class);
                                                    startActivity(intent);

                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });



                                    } else {
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


            }
            }
        });

        return view;
    }


}