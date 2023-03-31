package com.example.gaanbajaw;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;


public class SigninSignup extends Fragment {



    public SigninSignup() {
        // Required empty public constructor
    }

    Button signin,signup;
    RelativeLayout relativeLayout;
    LinearLayout linearLayout;
    FirebaseAuth auth;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin_signup, container, false);


        signin = view.findViewById(R.id.signin);
        signup = view.findViewById(R.id.signup);
        relativeLayout = view.findViewById(R.id.relative_layout);
        linearLayout = view.findViewById(R.id.inuplinear);
        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {

            Intent intent = new Intent(getContext(), OnlineSong.class);
            startActivity(intent);
        }



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin Signin = new signin();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.relative_layout, Signin);
                //fragmentTransaction.addToBackStack("SigninSignup");
                fragmentTransaction.commit();

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup Signup = new signup();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.relative_layout, Signup);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}