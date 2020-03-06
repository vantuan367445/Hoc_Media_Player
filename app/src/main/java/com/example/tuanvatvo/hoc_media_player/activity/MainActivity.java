package com.example.tuanvatvo.hoc_media_player.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuanvatvo.hoc_media_player.R;
import com.example.tuanvatvo.hoc_media_player.adapter.AdapterSong;
import com.example.tuanvatvo.hoc_media_player.model.ModelSong;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final  int MY_PERMISSION_REQUEST = 1;
    public static ArrayList<ModelSong> arraySong;
    ListView lvMainSong;
    AdapterSong adapterMainSong;
    TextView txtMain_titleSong,txtMain_artistSong,txtExtra_TitleSong;
    MediaPlayer mediaPlayerMain;
    ImageButton imgMain_PlayandPause,imgMain_Next;
    LinearLayout linearMain_TitleandArtist,linear_playSong,linearMain;
    ProgressBar proBarMainSong;
    private Handler myHandler = new Handler();
    private double timeStart = 0;
    private double timeEnd = 0;
    boolean repeat = true;
    boolean shuffle = false;
    boolean repeatOne = false;
    int position = 0;
    int paused = 0 ;
    int checkTT = 0; //  = 0 là dnag ở Main còn  = 1 là ở PlaySOng
    Animation animation = null;
    ImageView imgHinh;
    //
    SeekBar seekBarExtra_Song;
    TextView txtExtra_timeStartSong,txtExtra_timeEnd;
    ImageButton imgExtra_PlayandPause,imgExtra_Left,imgExtra_Right,imgExtra_ShuffleRepeat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();


    }

    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }
        else{
            addControls();
            addEventsMain();
            addEventsExtra();
        }
    }
    public void addControls(){
        // ánh xạ Main
        lvMainSong          = findViewById(R.id.lvMainSong);
        txtMain_titleSong = findViewById(R.id.txtMain_titleSong);
        txtMain_artistSong = findViewById(R.id.txtMain_artistSong);
        imgMain_PlayandPause      = findViewById(R.id.imgMain_PlayandPause);
        imgMain_Next       = findViewById(R.id.imgMain_Next);
        proBarMainSong = findViewById(R.id.proBarMainSong);
        linearMain_TitleandArtist    = findViewById(R.id.linearMain_TitleandArtist);
        linear_playSong          = findViewById(R.id.linear_playSong);
        linearMain                = findViewById(R.id.linearMain);
        linear_playSong.setVisibility(View.INVISIBLE); // ẩn
        // ãnh xạ Extra
        seekBarExtra_Song = findViewById(R.id.seekBarExtra_Song);
        txtExtra_timeStartSong = findViewById(R.id.txtExtra_timeStartSong);
        txtExtra_TitleSong = findViewById(R.id.txtExtra_TitleSong);
        txtExtra_timeEnd = findViewById(R.id.txtExtra_timeEnd);
        imgExtra_PlayandPause = findViewById(R.id.imgExtra_PlayandPause);
        imgExtra_Left = findViewById(R.id.imgExtra_Left);
        imgExtra_Right = findViewById(R.id.imgExtra_Right);
        imgExtra_ShuffleRepeat = findViewById(R.id.imgExtra_ShuffleRepeat);
        imgHinh = findViewById(R.id.imgHinh);

        animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.xoayhinh);


        arraySong = new ArrayList<>();
        getSong();
        if(arraySong.size() > 0){
            txtMain_titleSong.setText(arraySong.get(0).getTitle());
            txtExtra_TitleSong.setText(arraySong.get(0).getTitle());
            txtMain_artistSong.setText(arraySong.get(0).getArtist());
        }
        adapterMainSong = new AdapterSong(MainActivity.this,R.layout.item_song,arraySong);
        lvMainSong.setAdapter(adapterMainSong);

        // khởi tạo ban đầu cho Extra
        mediaPlayerMain=MediaPlayer.create(MainActivity.this, Uri.parse(arraySong.get(position).getLoacation()));
        timeStart = mediaPlayerMain.getCurrentPosition(); // trả về thời gian đang phát hiện tại
        timeEnd = mediaPlayerMain.getDuration(); // trả về thời lượng của bài hát

        proBarMainSong.setMax((int) timeEnd);
        seekBarExtra_Song.setMax((int) timeEnd);

        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        txtExtra_timeEnd.setText(format.format(timeEnd));

        txtMain_titleSong.setText(arraySong.get(position).getTitle());
        txtMain_artistSong.setText(arraySong.get(position).getArtist());

    }
    public void addEventsMain(){


        lvMainSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "play " + i, Toast.LENGTH_SHORT).show();
                position = i;
                doPlaySong();
            }
        });

        imgMain_PlayandPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayerMain == null){
                    KhoiTaoMediaPlayer();
                }
                else if(mediaPlayerMain.isPlaying()){
                    // nếu đang phát thì dừng lại
                    mediaPlayerMain.pause();
                    paused = mediaPlayerMain.getCurrentPosition();
                    imgMain_PlayandPause.setImageResource(R.drawable.play_24dp);
                    imgExtra_PlayandPause.setImageResource(R.drawable.play_32dp);
                    animation.cancel();
                }
                else{
                        // nếu đnag dừng lại thì phát
                    mediaPlayerMain.seekTo(paused);
                    mediaPlayerMain.start();
                    imgHinh.startAnimation(animation);
                    imgMain_PlayandPause.setImageResource(R.drawable.pause_24dp);
                    imgExtra_PlayandPause.setImageResource(R.drawable.pause_32dp);
                    myHandler.postDelayed(checkHetBai,1000);
                }




            }
        });

        imgMain_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                position++;
                if(position > arraySong.size()-1)
                    position = 0;
                if(mediaPlayerMain == null)
                    KhoiTaoMediaPlayer();
                else if(mediaPlayerMain.isPlaying()){
                    mediaPlayerMain.stop();
                    animation.cancel();
                    KhoiTaoMediaPlayer();
                }


            }
        });

        linearMain_TitleandArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkTT = 1;
                linear_playSong.setVisibility(View.VISIBLE);
                linearMain.setVisibility(View.INVISIBLE);
                lvMainSong.setVisibility(View.INVISIBLE);

            }
        });

        
        

    }
    public void addEventsExtra(){
        imgExtra_PlayandPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayerMain == null){
                    KhoiTaoMediaPlayer();
                }
                else if(mediaPlayerMain.isPlaying()){
                    animation.cancel();
                    mediaPlayerMain.pause();
                    paused = mediaPlayerMain.getCurrentPosition();
                    imgExtra_PlayandPause.setImageResource(R.drawable.play_32dp);
                    imgMain_PlayandPause.setImageResource(R.drawable.play_24dp);
                }
                else{
                    mediaPlayerMain.seekTo(paused);
                    mediaPlayerMain.start();
                    imgHinh.startAnimation(animation);
                    imgExtra_PlayandPause.setImageResource(R.drawable.pause_32dp);
                    imgMain_PlayandPause.setImageResource(R.drawable.pause_24dp);
                    myHandler.postDelayed(checkHetBai,1000);
                }

            }
        });

        imgExtra_Left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffle == true){
                    Random rd = new Random();
                    int high = arraySong.size();
                    position = rd.nextInt(high - 0)+0;
                    if(mediaPlayerMain == null)
                        KhoiTaoMediaPlayer();
                    else if(mediaPlayerMain.isPlaying()){
                        mediaPlayerMain.stop();
                        animation.cancel();
                        KhoiTaoMediaPlayer();
                    }
                }
                else{
                    position--;
                    if(position < 0)
                        position = arraySong.size()-1;

                    if(mediaPlayerMain == null)
                        KhoiTaoMediaPlayer();
                    else if(mediaPlayerMain.isPlaying()){
                        mediaPlayerMain.stop();
                        KhoiTaoMediaPlayer();
                    }
                }




            }
        });
        imgExtra_Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffle == true){
                    Random rd = new Random();
                    int high = arraySong.size();
                    position = rd.nextInt(high - 0)+0;
                    if(mediaPlayerMain == null)
                        KhoiTaoMediaPlayer();
                    else if(mediaPlayerMain.isPlaying()){
                        mediaPlayerMain.stop();
                        animation.cancel();
                        KhoiTaoMediaPlayer();
                    }
                }
                else{
                    position++;
                    if(position > arraySong.size()-1)
                        position = 0;
                    if(mediaPlayerMain == null)
                        KhoiTaoMediaPlayer();
                    else if(mediaPlayerMain.isPlaying()){
                        mediaPlayerMain.stop();
                        KhoiTaoMediaPlayer();
                    }
                }


            }
        });
        imgExtra_ShuffleRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //repeat - > shuffle - >repeatOne
                if(repeat == true){
                    repeat = false;
                    shuffle = true;
                    repeatOne = false;
                    imgExtra_ShuffleRepeat.setImageResource(R.drawable.shuffle_24dp);
                    Toast.makeText(MainActivity.this, "Bật trộn lẫn", Toast.LENGTH_SHORT).show();

                }
                else if(shuffle == true){
                    repeat = false;
                    shuffle = false;
                    repeatOne = true;
                    imgExtra_ShuffleRepeat.setImageResource(R.drawable.repeat_one_24dp);
                    Toast.makeText(MainActivity.this, "Lặp lại bài hiện tại", Toast.LENGTH_SHORT).show();

                }
                else if(repeatOne == true){
                    repeat = true;
                    shuffle = false;
                    repeatOne = false;
                    imgExtra_ShuffleRepeat.setImageResource(R.drawable.repeat_24dp);
                    Toast.makeText(MainActivity.this, "Trình tự bật", Toast.LENGTH_SHORT).show();

                }

            }
        });

        seekBarExtra_Song.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            // khi di di chuyển  or kéo thì k cập nhật khi buông ra mới cập nhật
                paused = seekBar.getProgress();
                mediaPlayerMain.seekTo(paused);
            }
        });
    }

    public void getSong(){
        ContentResolver contentResolver = getContentResolver();
        Uri songURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songURI,null,null,null,null);

        if(songCursor != null && songCursor.moveToNext()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);

                arraySong.add(new ModelSong(currentTitle,currentArtist,currentLocation));

            }while (songCursor.moveToNext());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission granter", Toast.LENGTH_SHORT).show();
                        addControls();
                        addEventsMain();
                        addEventsExtra();
                    }
                    else {
                        Toast.makeText(this, "No permisstion", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return;
                }
            }

        }
    }

    public  void doPlaySong(){
        if(mediaPlayerMain != null){
            mediaPlayerMain.stop();
            mediaPlayerMain.release();
        }
        KhoiTaoMediaPlayer();
    }

    private  void KhoiTaoMediaPlayer(){
        mediaPlayerMain=MediaPlayer.create(MainActivity.this, Uri.parse(arraySong.get(position).getLoacation()));
        mediaPlayerMain.start();
        timeStart = mediaPlayerMain.getCurrentPosition(); // trả về thời gian đang phát hiện tại
        timeEnd = mediaPlayerMain.getDuration(); // trả về thời lượng của bài hát
        animation.start();
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        txtExtra_timeEnd.setText(format.format(timeEnd));

        proBarMainSong.setMax((int) timeEnd);
        seekBarExtra_Song.setMax((int) timeEnd);


        txtMain_titleSong.setText(arraySong.get(position).getTitle());
        txtExtra_TitleSong.setText(arraySong.get(position).getTitle());
        txtMain_artistSong.setText(arraySong.get(position).getArtist());
        imgMain_PlayandPause.setImageResource(R.drawable.pause_24dp);
        imgExtra_PlayandPause.setImageResource(R.drawable.pause_32dp);
        myHandler.postDelayed(checkHetBai,1000);

    }


     private Runnable checkHetBai = new Runnable() {
    @Override
    public void run() {
        timeStart = mediaPlayerMain.getCurrentPosition();
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        txtExtra_timeStartSong.setText(format.format(timeStart));

        proBarMainSong.setProgress((int)timeStart);
        seekBarExtra_Song.setProgress((int) timeStart);


        myHandler.postDelayed(this,100);


        mediaPlayerMain.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(repeat == true){
                    // bật chế độ trình tự
                    position++;
                    if(position > arraySong.size() - 1)
                        position = 0;
                }
                else if( shuffle == true){
                    // bật chế độ xáo trộn
                    Random rd = new Random();
                    int high = arraySong.size();
                    position = rd.nextInt(high - 0)+0;

                }
                else {
                    // bật chế độ repeatOne
                     // không lam gì ca
                }
                KhoiTaoMediaPlayer();
                myHandler.postDelayed(checkHetBai,100);
            }
        });
    }
};

    @Override
    public void onBackPressed() {

        if(checkTT == 0){
            // dang oi Main
            checkTT = 1;

            mediaPlayerMain.stop();
            super.onBackPressed();

        }
        else{

            linear_playSong.setVisibility(View.INVISIBLE);
            linearMain.setVisibility(View.VISIBLE);
            lvMainSong.setVisibility(View.VISIBLE);
            checkTT = 0;
        }




    }
}
