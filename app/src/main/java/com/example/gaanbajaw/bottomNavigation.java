package com.example.gaanbajaw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottomNavigation extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private long backpress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

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
    }
    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(bottomNavigation.this,MainActivity.class);
//        overridePendingTransition(0,0);
//        startActivity(intent);
//        super.onBackPressed();
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