package com.cookandroid.mp3_project_02_w_hyunsung;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Fragment_Music extends Fragment {

    private MainActivity mainActivity;

    private ArrayList<MusicData> musicList = new ArrayList<>();

    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;

    private MusicDBHelper dbHelper;
    private MusicAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public static Fragment_Music newInstance(int fragNumber) {
        Fragment_Music fragment_music = new Fragment_Music();
        Bundle bundle = new Bundle();
        bundle.putInt("fragNumber", fragNumber);
        fragment_music.setArguments(bundle);
        return fragment_music;
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
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        findViewByIdFunc(view);

        dbHelper = MusicDBHelper.getInstance(mainActivity);
        //중복 방지 equals 를 통해 모든 음악파일 ArrayList 에 담음
        musicList = dbHelper.compareArrayList();

        adapter = new MusicAdapter(getActivity(), musicList);

        linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        //리사이클러뷰 어댑터 세팅
        recyclerViewListUpdate(musicList);
        //리사이클러뷰 클릭 이벤트
        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                mainActivity.setPlayerData(pos, 1);
            }
        });
        return view;
    }

    private void findViewByIdFunc(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        drawerLayout = view.findViewById(R.id.drawerLayout);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void recyclerViewListUpdate(ArrayList<MusicData> arrayList) {
        //어댑터에 데이터리스트 세팅
        adapter.setMusicData(arrayList);
        //recyclerView 에 어댑터 세팅
        recyclerView.setAdapter(adapter);
        //갱신
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}