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
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {

    public static Random random = new Random();
    public static final int LIST = 1;
    public static final int LIKE = 2;
    public static final int SEARCH = 3;

    private ViewPager2 viewPager2;
    private CircleIndicator3 indicator;
    private FragmentStateAdapter pagerAdapter;
    private MediaPlayer mPlayer = new MediaPlayer();

    private MusicDBHelper DBHelper;
    private MusicData musicData;
    private MusicData musicData_search;
    private MusicAdapter adapter;

    private DrawerLayout drawerLayout;
    private ImageView ivPlayerAlbumCover, imgDrawerMenuAlbumCover, ivBackCover;
    private TextView tvPlayerTitle, tvPlayerArtist, tvCurrentTime, tvDuration, tvCount;
    private SeekBar seekBar;
    private Button btnPrev, btnPlayPause, btnNext, btnLike, btnShuffle, btnMenu;
    private FrameLayout frameImg;

    private ArrayList<MusicData> musicList = new ArrayList<>();
    private ArrayList<MusicData> musicList_Like = new ArrayList<>();

    private int numberPage = 3;     //뷰페이저 갯수
    private int index;
    private boolean nowPlaying = false;     //재생 중 확인
    private boolean shuffle = false;         //램덤재생 확인


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //로딩화면 호출
        startLoadingActivity();
        //외부 접근 허용
        requestPermissionFunc();
        //id 연결
        findViewByIdFunc();
        //ViewPager2 세팅
        setViewPager2();
        //이벤트 적용
        eventHandlerFunc();
    }

    private void startLoadingActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }

    //버튼 클릭이벤트 처리
    private void eventHandlerFunc() {
        btnPlayPause.setOnClickListener((View v) -> {
            if (nowPlaying == true) {
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
            int nowDurationForSec = Integer.parseInt(sdfS.format(mPlayer.getCurrentPosition()));
            mPlayer.stop();
            mPlayer.reset();
            nowPlaying = false;
            btnPlayPause.setBackgroundResource(R.drawable.play);
            try {
                if (shuffle == false) {
                    if (nowDurationForSec <= 5) {
                        if (index == 0) {
                            index = musicList.size();
                        }
                        index--;
                        setPlayerData(index, LIST);
                    } else {
                        setPlayerData(index, LIST);
                    }
                } else {
                    if (nowDurationForSec <= 5) {
                        index = random.nextInt(musicList.size());
                        setPlayerData(index, LIST);
                    } else {
                        setPlayerData(index, LIST);
                    }
                }
            } catch (Exception e) {
                Log.d("Prev", e.getMessage());
            }
        });
        btnNext.setOnClickListener((View v) -> {
            mPlayer.stop();
            mPlayer.reset();
            nowPlaying = false;
            btnPlayPause.setBackgroundResource(R.drawable.play);
            try {
                if (shuffle == false) {
                    if (index == musicList.size() - 1) {
                        index = -1;
                    }
                    index++;
                    setPlayerData(index, LIST);
                } else {
                    index = random.nextInt(musicList.size());
                    setPlayerData(index, LIST);
                }
            } catch (Exception e) {
                Log.d("Next", e.getMessage());
            }
        });
        //시크바 변경
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean startTouch) {
                if (startTouch == true) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btnLike.setOnClickListener((View v) -> {
            try {
                if (musicData.getLiked() == 1) {
                    musicData.setLiked(0);
                    musicList_Like.remove(musicData);
                    btnLike.setBackgroundResource(R.drawable.outline_moon);
                } else {
                    musicData.setLiked(1);
                    musicList_Like.add(musicData);
                    btnLike.setBackgroundResource(R.drawable.baseline_moon);
                }
                DBHelper.updateMusicDataToDB(musicList);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "노래를 골라주세요", Toast.LENGTH_SHORT).show();
            }
        });
        frameImg.setOnClickListener((View v) -> {
            if (ivBackCover.getVisibility() == View.INVISIBLE) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(ivPlayerAlbumCover, "rotationY", 0, 180);
                animator.setDuration(1000);
                animator.start();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(1000);
                        ivPlayerAlbumCover.setVisibility(View.INVISIBLE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivBackCover.setVisibility(View.VISIBLE);
                                try {
                                    tvCount.setText(musicData.getPlayCount() + "번 플레이됨");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "노래를 골라주세요", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                thread.start();
            } else if (ivPlayerAlbumCover.getVisibility() == View.INVISIBLE) {
                tvCount.setText("");
                ObjectAnimator animator = ObjectAnimator.ofFloat(ivBackCover, "rotationY", 0, 180);
                animator.setDuration(1000);
                animator.start();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(1000);
                        ivBackCover.setVisibility(View.INVISIBLE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivPlayerAlbumCover.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
                thread.start();
            }
        });
        btnShuffle.setOnClickListener((View v) -> {
            if (shuffle == false) {
                shuffle = true;
                btnShuffle.setBackgroundResource(R.drawable.shuffle_yellow);
            } else {
                shuffle = false;
                btnShuffle.setBackgroundResource(R.drawable.shuffle_white);
            }
        });
        btnMenu.setOnClickListener((View v) -> {
            drawerLayout.openDrawer(Gravity.LEFT);
        });
    }

    private void requestPermissionFunc() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, MODE_PRIVATE);
    }

    //백버튼 터치시 뷰페이저 넘김
    @Override
    public void onBackPressed() {
        if (viewPager2.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
        }
    }

    private void setViewPager2() {
        pagerAdapter = new ViewPager2Adapter(this, numberPage);
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
                if (positionOffsetPixels == 0) {
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
                float myOffset = position * -(2 * pageOffset + pageMargin);
                if (viewPager2.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                    if (ViewCompat.getLayoutDirection(viewPager2) == ViewCompat.LAYOUT_DIRECTION_RTL) {
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
        btnLike = findViewById(R.id.btnLike);
        frameImg = findViewById(R.id.frameImg);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnMenu = findViewById(R.id.btnMenu);
        ivBackCover = findViewById(R.id.ivBackCover);
        tvCount = findViewById(R.id.tvCount);

        //DB Helper 설정 && 뮤직 리스트 꺼내오기
        DBHelper = MusicDBHelper.getInstance(MainActivity.this);
        musicList = DBHelper.compareArrayList();
        musicList_Like = DBHelper.saveLikeList();
        //DB에 뮤직 리스트 insert
        DBHelper.insertMusicDataToDB(musicList);
    }

    public void setMusicData_search(MusicData musicData_search) {
        this.musicData_search = musicData_search;
    }

    //재생화면 처리
    public void setPlayerData(int pos, int func) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        index = pos;
        mPlayer.stop();
        mPlayer.reset();


        if (func == LIST) {
            musicData = musicList.get(pos);
        } else if (func == LIKE) {
            musicData = DBHelper.saveLikeList().get(pos);
        } else if (func == SEARCH) {
            musicData = musicData_search;
        }
        musicData.setPlayCount(musicData.getPlayCount() + 1);
        DBHelper.updateCountDataToDB(musicData);

        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        tvPlayerTitle.setText(musicData.getTitle());
        tvPlayerArtist.setText(musicData.getArtist());
        tvDuration.setText(sdf.format(Integer.parseInt(musicData.getDuration())));

        if (musicData.getLiked() == 1) {
            btnLike.setBackgroundResource(R.drawable.baseline_moon);
        } else {
            btnLike.setBackgroundResource(R.drawable.outline_moon);
        }

        if (shuffle == true) {
            btnShuffle.setBackgroundResource(R.drawable.shuffle_yellow);
        } else {
            btnShuffle.setBackgroundResource(R.drawable.shuffle_white);
        }
        //recycler adapter
        adapter = new MusicAdapter(this, musicList);
        // 앨범 커버 이미지 세팅
        Bitmap coverImg = adapter.getCoverImg(this, Integer.parseInt(musicData.getAlbumCover()), 400);
        if (coverImg != null) {
            ivPlayerAlbumCover.setImageBitmap(coverImg);
            imgDrawerMenuAlbumCover.setImageBitmap(coverImg);
        } else {
            ivPlayerAlbumCover.setImageResource(R.drawable.moon);
            imgDrawerMenuAlbumCover.setImageResource(R.drawable.moon);
        }
        //음악 재생
        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
        try {
            mPlayer.setDataSource(this, musicURI);
            mPlayer.prepare();
            mPlayer.start();
            btnPlayPause.setBackgroundResource(R.drawable.pause);
            nowPlaying = true;
            seekBar.setProgress(0);
            seekBar.setMax(Integer.parseInt(musicData.getDuration()));
            setSeekBarThread();
            //재생완료 리스너
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    btnNext.callOnClick();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //시크바 스레드에 관한 함수
    private void setSeekBarThread() {
        Thread thread = new Thread(new Runnable() {
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

            @Override
            public void run() {
                while (mPlayer.isPlaying()) {
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}