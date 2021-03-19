package com.cookandroid.mp3_project_02_w_hyunsung;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private CircleIndicator3 indicator;
    private FragmentStateAdapter pagerAdapter;
    private MediaPlayer mPlayer = new MediaPlayer();

    private MainActivity mainActivity;
    private MusicDBHelper DBHelper;
    private MusicData musicData;
    private MusicAdapter adapter;

    private DrawerLayout drawerLayout;
    private ImageView ivPlayerAlbumCover, imgDrawerMenuAlbumCover;
    private TextView tvPlayerTitle, tvPlayerArtist, tvCurrentTime, tvDuration;
    private SeekBar seekBar;
    private Button btnPrev, btnPlayPause, btnNext;

    private ArrayList<MusicData> musicList = new ArrayList<>();
    private int numberPage = 3;
    private int index;
    private boolean nowPlaying = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissionFunc();

        findViewByIdFunc();

        setViewPager();

        DBHelper = MusicDBHelper.getInstance(getApplicationContext());
        musicList = DBHelper.compareArrayList();

        eventHandlerFunc();


    }

    private void eventHandlerFunc() {


        btnPlayPause.setOnClickListener((View v) -> {
            if(nowPlaying == true) {
                nowPlaying = false;
                mPlayer.pause();
                btnPlayPause.setBackgroundResource(R.drawable.play);
            } else {
                nowPlaying = true;
                mPlayer.start();
                btnPlayPause.setBackgroundResource(R.drawable.pause);
                setSeekBarThread();
            }
        });
        btnPrev.setOnClickListener((View v) -> {
            SimpleDateFormat sdfS = new SimpleDateFormat("ss");
            int nowDurationForSec =  Integer.parseInt(sdfS.format(mPlayer.getCurrentPosition()));

            mPlayer.stop();
            mPlayer.reset();
            nowPlaying =false;
            btnPlayPause.setBackgroundResource(R.drawable.play);
            try {
                if(nowDurationForSec <=5) {
                    if(index == 0)  {
                        index = musicList.size() -1;
                        setPlayerData(index);
                    } else {
                        index--;
                        setPlayerData(index);
                    }
                } else {
                    setPlayerData(index);
                }
            } catch (Exception e) {
                Log.d("Prev", e.getMessage());
            }
        });
        btnNext.setOnClickListener((View v) -> {
            mPlayer.stop();
            mPlayer.reset();
            nowPlaying =false;
            btnPlayPause.setBackgroundResource(R.drawable.play);
            try {
                if(index == musicList.size() -1) {
                    index = 0;
                    setPlayerData(index);
                } else {
                    index++;
                    setPlayerData(index);
                }
            } catch (Exception e) {
                Log.d("Next", e.getMessage());
            }
        });
    }

    private void requestPermissionFunc() {
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, MODE_PRIVATE);
    }

    private void setViewPager() {
        pagerAdapter = new FragmentAdapter(this, numberPage);
        viewPager2.setAdapter(pagerAdapter);

        indicator.setViewPager(viewPager2);
        indicator.createIndicators(numberPage, 0);

        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        viewPager2.setCurrentItem(100);
        viewPager2.setOffscreenPageLimit(2);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(positionOffsetPixels == 0) {
                    viewPager2.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                indicator.animatePageSelected(position % numberPage);
            }
        });

        final float pageMargin = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        final float pageOffset = getResources().getDimensionPixelOffset(R.dimen.offset);

        viewPager2.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float myOffset = position * - (2 * pageOffset + pageMargin);
                if(viewPager2.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                    if(ViewCompat.getLayoutDirection(viewPager2) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                        page.setTranslationX(-myOffset);
                    } else {
                        page.setTranslationX(myOffset);
                    }
                } else {
                    page.setTranslationY(myOffset);
                }
            }
        });
    }

    private void findViewByIdFunc() {
        viewPager2 = findViewById(R.id.viewPager2);
        indicator = findViewById(R.id.indicater);
        ivPlayerAlbumCover = findViewById(R.id.ivPlayerAlbumCover);
        tvPlayerTitle = findViewById(R.id.tvPlayerTitle);
        tvPlayerArtist = findViewById(R.id.tvPlayerArtist);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvDuration = findViewById(R.id.tvDuration);
        seekBar = findViewById(R.id.seekBar);
        btnNext = findViewById(R.id.btnNext);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPrev = findViewById(R.id.btnPrev);
        drawerLayout = findViewById(R.id.drawerLayout);
        imgDrawerMenuAlbumCover = findViewById(R.id.imgDrawerMenuAlbumCover);
    }

    public void setPlayerData(int pos) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        index = pos;

        mPlayer.stop();
        mPlayer.reset();

        musicData = musicList.get(pos);

        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

        tvPlayerTitle.setText(musicData.getTitle());
        tvPlayerArtist.setText(musicData.getArtist());
        tvDuration.setText(sdf.format(Integer.parseInt(musicData.getDuration())));

        adapter = new MusicAdapter(this, musicList);
        Bitmap coverImg = adapter.getCoverImg(this, Integer.parseInt(musicData.getAlbumCover()), 400);
        if(coverImg != null) {
            ivPlayerAlbumCover.setImageBitmap(coverImg);
            imgDrawerMenuAlbumCover.setImageBitmap(coverImg);
        } else {
            ivPlayerAlbumCover.setImageResource(R.drawable.unknown);
            imgDrawerMenuAlbumCover.setImageResource(R.drawable.unknown);
        }
        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
        try {
            mPlayer.setDataSource(this, musicURI);
            mPlayer.prepare();
            mPlayer.start();
            btnPlayPause.setBackgroundResource(R.drawable.pause);
            nowPlaying =true;
            seekBar.setProgress(0);
            seekBar.setMax(Integer.parseInt(musicData.getDuration()));
            setSeekBarThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSeekBarThread() {
        Thread thread = new Thread(new Runnable() {
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

            @Override
            public void run() {
                while(mPlayer.isPlaying()) {
                    seekBar.setProgress(mPlayer.getCurrentPosition());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCurrentTime.setText(sdf.format(mPlayer.getCurrentPosition()));
                        }
                    });
                    SystemClock.sleep(100);
                }
            }
        });
        thread.start();
    }
}