package com.example.gaanbajaw;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.gaanbajaw.AlbumDetailsAdapter.albumFiles;
import static com.example.gaanbajaw.ApplicationClass.ACTION_NEXT;
import static com.example.gaanbajaw.ApplicationClass.ACTION_PLAY;
import static com.example.gaanbajaw.ApplicationClass.ACTION_PREVIOU;
import static com.example.gaanbajaw.ApplicationClass.CHANNEL_ID_2;
import static com.example.gaanbajaw.MainActivity.musicFiles;
import static com.example.gaanbajaw.MainActivity.repeatBoolean;
import static com.example.gaanbajaw.MainActivity.shuffleBoolean;
import static com.example.gaanbajaw.MusicAdapter.mFiles;

public class PlayerActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {

    TextView song_name,artist_name,duration_player,durayion_total;
    ImageView cover_art,nextBtn,prevBtn,backBtn,shuffleBtn,repeatBtn,playPauseBtn,menu,back;
    //FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    //static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread,prevThread,nextThread;
    MusicService musicService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
       // getSupportActionBar().hide();
        initViews();
        getIntenMathod();
      //  musicService.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (musicService != null && fromUser){
                    musicService.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null){
                    int mCurrentPosition = musicService.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_player.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);


            }
        });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean){
                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.shuffle_off);
                }
                else {
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.shuffle);
                }

            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (repeatBoolean){

                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.repeat_off);
                }
                else {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.repeat);
                }
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongsFragment songsFragment = new SongsFragment();
                songsFragment.show(getSupportFragmentManager(),songsFragment.getTag());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setFullScreen() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {

        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void playThreadBtn() {

        playThread = new Thread(){

            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();


    }

    public void playPauseBtnClicked() {

        if (musicService.isPlaying()){

            playPauseBtn.setImageResource(R.drawable.music);
            musicService.shoeNitification(R.drawable.ic_play);

            musicService.pause();
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);


                }
            });
        }
        else {
            musicService.shoeNitification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);


                }
            });
        }
    }

    private void nextThreadBtn() {

        nextThread = new Thread(){

            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();


    }

    public void nextBtnClicked() {

       // mediaPlayer.start();

        if (musicService.isPlaying()){

            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1)% listSongs.size());

            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.creatMediaPlayer(position);
            mateData(uri);
            song_name.setText(listSongs.get(position).getTital());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);


                }
            });
            musicService.onComplete();
            musicService.shoeNitification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.pause);
            musicService.start();

        }
        else {

            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1)% listSongs.size());

            }

            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.creatMediaPlayer(position);
            mateData(uri);
            song_name.setText(listSongs.get(position).getTital());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);


                }
            });
            musicService.onComplete();
            musicService.shoeNitification(R.drawable.ic_play);
            playPauseBtn.setImageResource(R.drawable.music);

        }
    }

    private int getRandom(int i) {

        Random random = new Random();
        return random.nextInt(i+1);
    }

    private void prevThreadBtn() {

        prevThread = new Thread(){

            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();


    }

    public void prevBtnClicked() {

        //mediaPlayer.start();


        if (musicService.isPlaying()){

            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1)< 0 ? (listSongs.size() - 1) : (position -1));
            }

           // position = ((position - 1)< 0 ? (listSongs.size() - 1) : (position -1));
            musicService.creatMediaPlayer(position);
            mateData(uri);
            song_name.setText(listSongs.get(position).getTital());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);


                }
            });
            musicService.onComplete();
            musicService.shoeNitification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.pause);
            musicService.start();

        }
        else {

            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1)< 0 ? (listSongs.size() - 1) : (position -1));
            }

            // position = ((position - 1)< 0 ? (listSongs.size() - 1) : (position -1));
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.creatMediaPlayer(position);
            mateData(uri);
            song_name.setText(listSongs.get(position).getTital());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);


                }
            });
            musicService.onComplete();
            musicService.shoeNitification(R.drawable.ic_play);
            playPauseBtn.setImageResource(R.drawable.music);

        }
    }

    public String formattedTime(int mCurrentPosition) {

                String totalout = "";
                String totalNew = "";
                String seconds = String.valueOf(mCurrentPosition % 60);
                String minutes = String.valueOf(mCurrentPosition/60);
                totalout = minutes + ":" + seconds;
                totalNew = minutes + ":" + "0" +seconds;

                if (seconds.length() == 1){
                    return totalNew;
                }
                else {
                   return totalout;
                }
            }

    private void getIntenMathod() {

        position = getIntent().getIntExtra("position",-1);
        String sender = getIntent().getStringExtra("sender");

        if (sender!= null && sender.equals("albumDetails")){

            listSongs = albumFiles;
        }
        else {
            listSongs = mFiles;
        }

       // listSongs = musicFiles;

        if (listSongs != null){

            playPauseBtn.setImageResource(R.drawable.pause);
            uri = Uri.parse(listSongs.get(position).getPath());

        }
        Intent intent = new Intent(this,MusicService.class);
        intent.putExtra("servicePosition",position);
        startService(intent);



    }

    private void initViews() {

        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_player = findViewById(R.id.durationPlayed);
        durayion_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
        menu = findViewById(R.id.menu_btn);
        back = findViewById(R.id.back_btn);
    }

    private void mateData(Uri uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration())/1000;
        durayion_total.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;

        if (art != null){

            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,cover_art,bitmap);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null){
                        ImageView gredient = findViewById(R.id.ImageViewGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        gredient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());

                    }
                    else {
                        ImageView gredient = findViewById(R.id.ImageViewGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        gredient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }

                }
            });
        }
        else {
            Glide.with(this).asBitmap().load(R.drawable.logo).into(cover_art);
            ImageView gredient = findViewById(R.id.ImageViewGradient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gredient.setBackgroundResource(R.drawable.gredient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }


    }
    public void ImageAnimation(Context context,ImageView imageView, Bitmap bitmap){

        Animation aniOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        //Animation aniIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

        aniOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Glide.with(context).load(bitmap).into(imageView);
//                aniIn.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });

              //  imageView.startAnimation(aniIn);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(aniOut);



    }




    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);
        Toast.makeText(this, "Connected "+musicService, Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration()/1000);
        mateData(uri);
        song_name.setText(listSongs.get(position).getTital());
        artist_name.setText(listSongs.get(position).getArtist());
        musicService.onComplete();
        musicService.shoeNitification(R.drawable.ic_play);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        musicService = null;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
                Intent intent = new Intent(PlayerActivity.this,MainActivity.class);
                startActivity(intent);


    }
}