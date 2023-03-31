package com.example.gaanbajaw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    public static final int REQUEST_CODE = 1;
    static ArrayList<MusicFiles> musicFiles;
    static boolean shuffleBoolean = false, repeatBoolean = false;
    static ArrayList<MusicFiles> albums = new ArrayList<>();
    private String My_SORT_PREF = "SortOrder";
    BottomNavigationView bottomNavigationView;
    ImageView sort;
    private long backpress;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static boolean SHOW_MINI_PLAYER = false;
    public static String ARTIST_TO_FRAG = null;
    public static String SONG_NAME_TO_FRAG = null;
    public static String PATH_TO_FRAG = null;
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission();

        sort = findViewById(R.id.sortBY);

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, sort);
                popupMenu.getMenuInflater().inflate(R.menu.search,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        SharedPreferences.Editor editor = getSharedPreferences(My_SORT_PREF,MODE_PRIVATE).edit();

                        switch (item.getItemId()){

                            case R.id.by_name :
                                editor.putString("sorting","sortByName");
                                editor.apply();
                                recreate();
                                break;

                            case R.id.by_date :
                                editor.putString("sorting","sortByDate");
                                editor.apply();
                                recreate();
                                break;

                            case R.id.by_size :
                                editor.putString("sorting","sortBySize");
                                editor.apply();
                                recreate();
                                break;


                        }
                        return true;
                    }

                });
                popupMenu.show();



            }
        });

        SearchView searchView = findViewById(R.id.search);
        //searchView.setOnQueryTextListener(this);
       // return super.onCreateOptionsMenu(menu);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String userInput = newText.toLowerCase();
                ArrayList<MusicFiles> myFiles = new ArrayList<>();
                for (MusicFiles song : musicFiles){
                    if (song.getTital().toLowerCase().contains(userInput)){
                        myFiles.add(song);
                    }
                }
                SongsFragment.musicAdapter.updateList(myFiles);

                return true;

            }
        });




//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.deviceMusic);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                switch (item.getItemId()){
//
//                    case R.id.onlineMusic :
//                        startActivity(new Intent(getApplicationContext(),signinActivity.class));
//                        overridePendingTransition(0,0);
//                        return true;
//
//                    case R.id.deviceMusic :
//                        return true;
//
//
//
//                }
//                return false;
//            }
//        });


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


    private void permission() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
        else {

            musicFiles = getAllAudio(this);
            initViewPager();

        }
    }
//    private void permission2() {
//
//        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
//        != PackageManager.PERMISSION_GRANTED){
//
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    REQUEST_CODE);
//        }
//        else {
//
//            musicFiles = getAllAudio(this);
//
//
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){


                musicFiles = getAllAudio(this);
                initViewPager();


            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }

    private void initViewPager() {

        ViewPager viewPager = findViewById(R.id.viewpager);
       TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(),"Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(),"Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }



    public static class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title){

            fragments.add(fragment);
            titles.add(title);

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public ArrayList<MusicFiles> getAllAudio(Context context){

       SharedPreferences preferences = getSharedPreferences(My_SORT_PREF,MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting","sortByName");
        String order = null;
        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;



        switch (sortOrder){

            case  "sortByName" :
               order = MediaStore.MediaColumns.TITLE + "";
                break;

            case  "sortByDate" :
                order = MediaStore.MediaColumns.DATE_ADDED + "";
                break;

            case  "sortBySize" :
                order = MediaStore.MediaColumns.SIZE + "";
                break;
        }
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
        };



        Cursor cursor = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                order);

        if (cursor != null){
            while (cursor.moveToNext()){

                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(path,title,artist,album,duration,id);
                Log.e("Path : "+path,"Album : "+album);
                tempAudioList.add(musicFiles);
                if (!duplicate.contains(album)){
                    albums.add(musicFiles);
                    duplicate.add(album);
                }

            }
            cursor.close();
        }
        return tempAudioList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE);
        String path = preferences.getString(MUSIC_FILE,null);
        String artist = preferences.getString(ARTIST_NAME,null);
        String song_name = preferences.getString(SONG_NAME,null);

        if (path != null){
            SHOW_MINI_PLAYER = true;
            PATH_TO_FRAG = path;
            ARTIST_TO_FRAG = artist;
            SONG_NAME_TO_FRAG = song_name;
        }
        else {
            SHOW_MINI_PLAYER = false;
            PATH_TO_FRAG = null;
            ARTIST_TO_FRAG = null;
            SONG_NAME_TO_FRAG = null;
        }
    }
    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search,menu);
//        MenuItem menuItem = menu.findItem(R.id.sort_option);
//        return super.onCreateOptionsMenu(menu);
//    }
//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        String userInput = newText.toLowerCase();
//        ArrayList<MusicFiles> myFiles = new ArrayList<>();
//        for (MusicFiles song : musicFiles){
//            if (song.getTital().toLowerCase().contains(userInput)){
//                myFiles.add(song);
//            }
//        }
//        SongsFragment.musicAdapter.updateList(myFiles);
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        SharedPreferences.Editor editor = getSharedPreferences(My_SORT_PREF,MODE_PRIVATE).edit();
//
//        switch (item.getItemId()){
//
//            case R.id.by_name :
//                editor.putString("sorting","sortByName");
//                editor.apply();
//                this.recreate();
//                break;
//
//            case R.id.by_date :
//                editor.putString("sorting","sortByDate");
//                editor.apply();
//                this.recreate();
//                break;
//
//            case R.id.by_size :
//                editor.putString("sorting","sortBySize");
//                editor.apply();
//                this.recreate();
//                break;
//
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
}