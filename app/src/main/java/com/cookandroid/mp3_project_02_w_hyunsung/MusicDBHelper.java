package com.cookandroid.mp3_project_02_w_hyunsung;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MusicDBHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String DB_NAME = "musicDB";
    private static final int VERSION = 1;

    // 싱글톤
    private static MusicDBHelper musicDBHelper;

    private MusicDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    public static MusicDBHelper getInstance(Context context) {
        if (musicDBHelper == null) {
            musicDBHelper = new MusicDBHelper(context);
        }
        return musicDBHelper;
    }

    // 테이블 생성
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE musicTBL(" +
                        "id VARCHAR(100) PRIMARY KEY," +
                        "artist VARCHAR(100)," +
                        "title VARCHAR(100)," +
                        "albumArt VARCHAR(100)," +
                        "duration VARCHAR(100)," +
                        "click INTEGER," +
                        "liked INTEGER );");

    }

    //테이블을 삭제한다.
    //i는 old ver i1은 new ver. 버전이 바뀔때 호출된다.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS musicTBL");
        onCreate(sqLiteDatabase);
    }

    // DB Select
    public ArrayList<MusicData> selectMusicTbl() {

        ArrayList<MusicData> musicDBArrayList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // 쿼리문 입력하고 커서 리턴 받음
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM musicTBL;", null);

        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));
            musicDBArrayList.add(musicData);
        }

        cursor.close();
        return musicDBArrayList;
    }

    //검색결과 출력 query
    public ArrayList<MusicData> searchMusicTbl(String s) {
        ArrayList<MusicData> musicDBArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //입력후 리턴받음
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM musicTBL WHERE title LIKE '%" + s + "%';", null);

        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));
            musicDBArrayList.add(musicData);
        }

        cursor.close();
        return musicDBArrayList;
    }

    // DB 삽입
    public boolean insertMusicDataToDB(ArrayList<MusicData> arrayList) {

        boolean returnValue = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        try {
            for (MusicData data : arrayList) {
                //DB 에서 리스트 가져오기
                ArrayList<MusicData> dbList = selectMusicTbl();
                //DB 에 속해있는 요소인지 확인
                if (dbList.contains(data) == false) {
                    String query = "INSERT INTO musicTBL VALUES('" + data.getId() + "','" + data.getArtist() + "','" + data.getTitle() + "','" + data.getAlbumCover() + "','" + data.getDuration() + "'," + 0 + "," + 0 + ");";
                    //쿼리문 작성해서 넘김
                    //예외발생시 SQLException
                    sqLiteDatabase.execSQL(query);
                }
            }
            returnValue = true;
        } catch (Exception e) {
            returnValue = false;
        }
        return returnValue;
    }

    // DB 업데이트
    public boolean updateMusicDataToDB(ArrayList<MusicData> arrayList) {
        boolean returnValue = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            for (MusicData data : arrayList) {
                String query = "UPDATE musicTBL SET click = " + data.getPlayCount() + ", liked = " + data.getLiked() + " WHERE id = '" + data.getId() + "';";
                sqLiteDatabase.execSQL(query);
            }
            returnValue = true;
        } catch (Exception e) {
            return false;
        }

        return returnValue;
    }

    public boolean updateCountDataToDB(MusicData data) {
        boolean returnValue = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            String query = "UPDATE musicTBL SET click = " + data.getPlayCount() + ", liked = " + data.getLiked() + " WHERE id = '" + data.getId() + "';";
            sqLiteDatabase.execSQL(query);
            returnValue = true;
        } catch (Exception e) {
            return false;
        }
        return returnValue;
    }

    // sdCard 안의 음악을 검색한다
    public ArrayList<MusicData> findMusic() {
        ArrayList<MusicData> sdCardList = new ArrayList<>();

        String[] data = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};

        //전체 영역에서 음악 가져오기
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, data[2] + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {

                //음악 데이터 가져오기
                String id = cursor.getString(cursor.getColumnIndex(data[0]));
                String artist = cursor.getString(cursor.getColumnIndex(data[1]));
                String title = cursor.getString(cursor.getColumnIndex(data[2]));
                String albumArt = cursor.getString(cursor.getColumnIndex(data[3]));
                String duration = cursor.getString(cursor.getColumnIndex(data[4]));

                MusicData mData = new MusicData(id, artist, title, albumArt, duration, 0, 0);
                sdCardList.add(mData);
            }
        }

        return sdCardList;
    }

    // 좋아요 리스트 저장
    public ArrayList<MusicData> saveLikeList() {

        ArrayList<MusicData> musicDBArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // 쿼리문 입력하고 커서 리턴 받음
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM musicTBL WHERE liked = 1;", null);

        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));
            musicDBArrayList.add(musicData);
        }

        cursor.close();
        return musicDBArrayList;
    }


    //sdcard 에서 검색한 음악과 DB를 비교해서 중복되지 않은 플레이리스트를 리턴
    public ArrayList<MusicData> compareArrayList() {
        //sdcard 에서 가져옴
        ArrayList<MusicData> sdCardList = findMusic();
        //database 에서 가져옴
        ArrayList<MusicData> dbList = selectMusicTbl();

        //DB가 비었다면 sdcard 리스트 리턴
        if (dbList.isEmpty()) {
            return sdCardList;
        }
        //DB가 이미 sdcard 정보를 가지고 있다면 DB 리스트를 리턴
        //MusicData 에 equals 오버라이딩 필수
        if (dbList.containsAll(sdCardList)) {
            return dbList;
        }
        // 두 리스트를 비교후 중복되지 않은 값을 DB 리스트에 추가후 리턴
        int size = dbList.size();
        for (int i = 0; i < size; ++i) {
            if (dbList.contains(sdCardList.get(i))) {
                continue;
            }
            dbList.add(sdCardList.get(i));
            ++size;
        }
        return dbList;
    }
}