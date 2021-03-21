package com.cookandroid.mp3_project_02_w_hyunsung;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Fragment_Like extends Fragment {

    private MainActivity mainActivity;
    private LinearLayoutManager layoutManager_like;
    private MusicAdapter adapter_like;
    private MusicDBHelper dbHelper;

    private RecyclerView recyclerViewLike;

    private ArrayList<MusicData> musicList_like = new ArrayList<>();

    public static Fragment_Like newInstance(int fragNumber) {
        Fragment_Like fragment_Like = new Fragment_Like();
        Bundle bundle = new Bundle();
        bundle.putInt("fragNumber", fragNumber);
        fragment_Like.setArguments(bundle);
        return fragment_Like;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_like, container, false);

        findViewByIdFunc(view);

        setAdapterLayoutManager();

        return view;
    }

    private void eventHandlerFunc() {
        //리사이클러뷰 클릭 이벤트
        adapter_like.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                mainActivity.setPlayerData(pos, MainActivity.LIKE);
            }
        });
    }

    private void setAdapterLayoutManager() {
        //DB Helper 인스턴스
        dbHelper = MusicDBHelper.getInstance(mainActivity);
        //어뎁터 설정
        adapter_like = new MusicAdapter(getActivity());
        //레이아웃 매니저 설정
        layoutManager_like = new LinearLayoutManager(getActivity());
        //리사이클러뷰에 어댑터, 매니저 세팅
        recyclerViewLike.setLayoutManager(layoutManager_like);
        likeRecyclerViewListUpdate(getMusicList_like());
    }

    //id 설정
    private void findViewByIdFunc(View view) {
        recyclerViewLike = view.findViewById(R.id.recyclerViewLike);
    }


    private void likeRecyclerViewListUpdate(ArrayList<MusicData> arrayList) {

        // 어댑터에 데이터리스트 세팅
        adapter_like.setMusicData(arrayList);

        // recyclerView에 어댑터 세팅
        recyclerViewLike.setAdapter(adapter_like);
        adapter_like.notifyDataSetChanged();
    }

    public ArrayList<MusicData> getMusicList_like() {
        musicList_like = dbHelper.saveLikeList();
        return musicList_like;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
