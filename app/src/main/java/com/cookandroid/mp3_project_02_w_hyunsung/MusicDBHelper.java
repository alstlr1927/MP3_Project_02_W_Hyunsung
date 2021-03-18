package com.cookandroid.mp3_project_02_w_hyunsung;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

import java.util.ArrayList;

public class MusicDBHelper extends SQLiteOpenHelper {
    private Context context;

    //singleTone
    private static MusicDBHelper musicDBHelper;

    private MusicDBHelper(Context context) {
        super(context ,"musicDB", null, 1);
        this.context = context;
    }

    public static MusicDBHelper getInstance(Context context) {
        if(musicDBHelper == null) {
            musicDBHelper = new MusicDBHelper(context);
        }
        return musicDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<MusicData> findMusicFile() {
        ArrayList<MusicData> sdCardList = new ArrayList<>();

        String[] data = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, data[2] + " ASC");

        if(cursor != null) {
            while(cursor.moveToNext()) {

                String id = cursor.getString(cursor.getColumnIndex(data[0]));
                String artist = cursor.getString(cursor.getColumnIndex(data[1]));
                String title = cursor.getString(cursor.getColumnIndex(data[2]));
                String albumCover = cursor.getString(cursor.getColumnIndex(data[3]));
                String duration = cursor.getString(cursor.getColumnIndex(data[4]));

                MusicData musicData = new MusicData(id, artist, title, albumCover, duration, 0);

                sdCardList.add(musicData);
            }
        }
        return sdCardList;
    }
}
