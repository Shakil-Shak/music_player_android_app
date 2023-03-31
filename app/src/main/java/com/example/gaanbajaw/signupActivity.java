package com.example.gaanbajaw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class signupActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    Button signup,uplode;
    EditText name,email,password;
    TextView songname,signin1;
    Uri uri;
    String songurl,songName;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User");

        dialog = new ProgressDialog(signupActivity.this);
        dialog.setTitle("Creating account");
        dialog.setMessage("We are creating your account");

        signup = findViewById(R.id.regester);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        songname = findViewById(R.id.songName);
        uplode = findViewById(R.id.FirstUplode);
        signin1 = findViewById(R.id.signin);

        uplode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickSong();

            }
        });


        signin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signupActivity.this,signinActivity.class);
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
                        startActivity(new Intent(signupActivity.this,MainActivity.class));
                        overridePendingTransition(0,0);

                        return false;


                }
                return true;
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().equals("")) {
                    Toast.makeText(signupActivity.this, "Please enter Name", Toast.LENGTH_SHORT).show();
                }
                else if (email.getText().toString().equals("")) {
                    Toast.makeText(signupActivity.this, "Please enter mail", Toast.LENGTH_SHORT).show();
                }
                else if (password.getText().toString().equals("")) {
                    Toast.makeText(signupActivity.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                }
                else if (songname.getText().toString().equals("")) {
                    Toast.makeText(signupActivity.this, "Please uplode a song", Toast.LENGTH_SHORT).show();
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

                                                    Toast.makeText(signupActivity.this, "Regestration Successfully", Toast.LENGTH_SHORT).show();
                                                    uploadSongToFirebaseStorage();



                                                    name.setText("");
                                                    email.setText("");
                                                    password.setText("");

//                                                    Intent intent = new Intent(signupActivity.this, OnlineSong.class);
//                                                    startActivity(intent);

                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(signupActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });



                                    } else {
                                        Toast.makeText(signupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }
            }
        });
    }
    private void pickSong() {

        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode ==1){
            if(resultCode ==RESULT_OK){

                uri = data.getData();

                Cursor mcursor = getApplicationContext().getContentResolver()
                        .query(uri,null,null,null,null);

                int indexedname = mcursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                mcursor.moveToFirst();


                 songName = mcursor.getString(indexedname);
                songname.setText(songName);
                mcursor.close();

                //uploadSongToFirebaseStorage();


            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadSongToFirebaseStorage() {

        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Songs").child(currentuser).child(uri.getLastPathSegment());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlSong = uriTask.getResult();
                songurl = urlSong.toString();

                uploadDetailsToDatabase();
                progressDialog.dismiss();





            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(signupActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progres = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                int currentProgress = (int)progres;
                progressDialog.setMessage("Uploaded: "+currentProgress+"%");
            }
        });
    }

    private void uploadDetailsToDatabase() {

        Song songObj = new Song(songName,songurl);
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.getReference().child("User").child(currentuser).child("Songs").push().setValue(songObj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(signupActivity.this, "Song Uploaded", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(signupActivity.this, OnlineSong.class);
                    startActivity(intent);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(signupActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });



    }



}