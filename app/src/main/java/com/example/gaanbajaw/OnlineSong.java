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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class OnlineSong extends AppCompatActivity {

    ImageView singout,uplode,back;
    FirebaseAuth auth;
    BottomNavigationView bottomNavigationView;
    Uri uri;
    String songName,songUrl;
    FirebaseDatabase database;
    ListView listView;
    ArrayList<String> arrayListSongsName = new ArrayList<>();
    ArrayList<String> arrayListSongUrl = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    DatabaseReference databaseReference;
    JcPlayerView jcPlayerView;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    private long backpress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_song);

        singout = findViewById(R.id.logout);
        uplode = findViewById(R.id.uplode);
        back = findViewById(R.id.back_btn);

        auth = FirebaseAuth.getInstance();
       // databaseReference = FirebaseDatabase.getInstance();
        listView = findViewById(R.id.myListView);
        database = FirebaseDatabase.getInstance();
        jcPlayerView = findViewById(R.id.jcplayer);



        try {
            retriveSongs();

        }
        catch (Exception e){
            Toast.makeText(this, "Uplode song", Toast.LENGTH_SHORT).show();
        }




            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    jcPlayerView.playAudio(jcAudios.get(position));
                    jcPlayerView.setVisibility(View.VISIBLE);
                    // jcPlayerView.createNotification();
                }
            });



            singout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    auth.signOut();

                    Intent intent = new Intent(OnlineSong.this, MainActivity.class);
                    startActivity(intent);

//                pickSong();
                }
            });

            uplode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pickSong();

                }
            });




//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.onlineMusic);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                switch (item.getItemId()){
//
//                    case R.id.onlineMusic :
//                        return true;
//
//                    case R.id.deviceMusic :
//                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                        overridePendingTransition(0,0);
//
//                        return true;
//
//
//                }
//                return false;
//            }
//        });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(OnlineSong.this,MainActivity.class);
                    startActivity(intent);
                }
            });

        }











    private void retriveSongs() {



            String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(currentuser).child("Songs");



                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {


                            Song songObj = ds.getValue(Song.class);
                            arrayListSongsName.add(songObj.getSongName());
                            arrayListSongUrl.add(songObj.getSongUrl());
                            jcAudios.add(JcAudio.createFromURL(songObj.getSongName(), songObj.getSongUrl()));


                        }

                        arrayAdapter = new ArrayAdapter<String>(OnlineSong.this, android.R.layout.simple_list_item_1, arrayListSongsName) {


                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                                textView.setSingleLine(true);
                                textView.setMaxLines(1);


                                return view;
                            }
                        };
                        jcPlayerView.initPlaylist(jcAudios, null);
                        listView.setAdapter(arrayAdapter);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                mcursor.close();

             uploadSongToFirebaseStorage();


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
                songUrl = urlSong.toString();

                uploadDetailsToDatabase();
                progressDialog.dismiss();





            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OnlineSong.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

        Song songObj = new Song(songName,songUrl);
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.getReference().child("User").child(currentuser).child("Songs").push().setValue(songObj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(OnlineSong.this, "Song Uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OnlineSong.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });



    }


    @Override
    public void onBackPressed() {

        if(backpress+2000 > System.currentTimeMillis()){

            super.onBackPressed();
            finishAffinity();
        }
        else {

            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        }

        backpress = System.currentTimeMillis();


    }
}