package com.cookandroid.mp3_project_02_w_hyunsung;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<MusicData> musicData;
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener;

    public MusicAdapter(Context context) {
        this.context = context;
    }

    public MusicAdapter(Context context, ArrayList<MusicData> musicData) {
        this.context = context;
        this.musicData = musicData;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        //앨범 이미지 비트맵으로 만들기
        Bitmap albumImg = getCoverImg(context, Integer.parseInt(musicData.get(position).getAlbumCover()), 200);
        if (albumImg != null) {
            holder.albumCover.setImageBitmap(albumImg);
        }
        //리사이클러뷰에 보여줘야할 정보 세팅
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        holder.artist.setText(musicData.get(position).getArtist());
        holder.title.setText(musicData.get(position).getTitle());
        holder.duration.setText(sdf.format(Integer.parseInt(musicData.get(position).getDuration())));
        holder.itemView.setTag(position);

    }

    //앨범아트 가져오는 함수
    public Bitmap getCoverImg(Context context, int albumArt, int imgMaxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /*컨텐트 프로바이더(Content Provider)는 앱 간의 데이터 공유를 위해 사용됨.
        특정 앱이 다른 앱의 데이터를 직접 접근해서 사용할 수 없기 때문에
        무조건 컨텐트 프로바이더를 통해 다른 앱의 데이터를 사용해야만 한다.
        다른 앱의 데이터를 사용하고자 하는 앱에서는 URI 를 이용하여 컨텐트 리졸버(Content Resolver)를 통해
        다른 앱의 컨텐트 프로바이더에게 데이터를 요청하게 되는데
        요청받은 컨텐트 프로바이더는 URI 를 확인하고 내부에서 데이터를 꺼내어 컨텐트 리졸버에게 전달한다.
        */

        ContentResolver contentResolver = context.getContentResolver();

        // 앨범 커버는 별도로 생성 (uri 제공 안함)
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + albumArt);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = contentResolver.openFileDescriptor(uri, "r");

                //true 면 비트맵객체에 메모리를 할당하지 않아서 비트맵을 반환하지 않음.
                //다만 options fields 는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있음.
                options.inJustDecodeBounds = true;

                int scale = 0;
                if (options.outHeight > imgMaxSize || options.outWidth > imgMaxSize) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(imgMaxSize /
                            (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                //true 면 비트맵을 만들지 않고 해당이미지의 가로, 세로, Mime type 등의 정보만 가져옴
                options.inJustDecodeBounds = false;
                //이미지의 원본사이즈를 설정된 스케일로 축소
                options.inSampleSize = scale;

                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

                if (bitmap != null) {
                    if (options.outWidth != imgMaxSize || options.outHeight != imgMaxSize) {
                        Bitmap map = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);
                        bitmap.recycle();
                        bitmap = map;
                    }
                }
                return bitmap;
            } catch (FileNotFoundException e) {
                Log.d("MainAdapter", "Content Resolver Error!");
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return (musicData != null) ? musicData.size() : 0;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView albumCover;
        TextView title;
        TextView artist;
        TextView duration;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.albumCover = itemView.findViewById(R.id.item_ivAlbumCover);
            this.title = itemView.findViewById(R.id.item_tvTitle);
            this.artist = itemView.findViewById(R.id.item_tvArtist);
            this.duration = itemView.findViewById(R.id.item_tvDuration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, pos);
                    }
                }
            });
        }
    }

    public void setMusicData(ArrayList<MusicData> musicData) {
        this.musicData = musicData;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    //OnItemClickListener 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener Listener) {
        this.mListener = Listener;
    }
}
