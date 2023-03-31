package com.example.gaanbajaw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class signin extends Fragment {


    public signin() {
        // Required empty public constructor
    }

    ProgressDialog dialog;
    FirebaseAuth auth;
    Button signin;
    EditText mail, password;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("LOGIN");
        dialog.setMessage("LOGIN to your account");
        signin = view.findViewById(R.id.signin);
        mail = view.findViewById(R.id.mail);
        password = view.findViewById(R.id.password);

//        SigninSignup signinSignup = new SigninSignup();
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//       // transaction.replace(R.id.relative_layout, signinSignup);
//        transaction.addToBackStack(signinSignup);
//        transaction.commit();



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mail.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter your G-suit mail", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter your Password", Toast.LENGTH_SHORT).show();

                } else {

                    dialog.show();
                    auth.signInWithEmailAndPassword(mail.getText().toString(), password.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    dialog.dismiss();

                                    if (task.isSuccessful()) {

                                        Intent intent = new Intent(getContext(), OnlineSong.class);
                                        startActivity(intent);
                                    } else {

                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }


            }
        });
        if (auth.getCurrentUser() != null) {

            Intent intent = new Intent(getContext(), OnlineSong.class);
            startActivity(intent);
        }


        return view;
}


}