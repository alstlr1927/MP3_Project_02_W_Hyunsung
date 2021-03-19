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

    private ArrayList<MusicData> musicList;

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
        mainActivity = (MainActivity)getActivity();
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

        musicList = dbHelper.findMusic();

        adapter = new MusicAdapter(getActivity(), musicList);

        linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                mainActivity.setPlayerData(pos);
//                drawerLayout.closeDrawer(Gravity.LEFT);
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

    @Override
    public void onStop() {
        super.onStop();
    }
}
