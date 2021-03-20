package com.cookandroid.mp3_project_02_w_hyunsung;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        mainActivity = (MainActivity)getActivity();
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

        dbHelper = MusicDBHelper.getInstance(mainActivity);

        musicList_like = dbHelper.saveLikeList();

        adapter_like = new MusicAdapter(getActivity(), musicList_like);

        layoutManager_like = new LinearLayoutManager(getActivity());

        recyclerViewLike.setAdapter(adapter_like);
        recyclerViewLike.setLayoutManager(layoutManager_like);

        likeRecyclerViewListUpdate(musicList_like);

        return view;
    }

    private void findViewByIdFunc(View view) {
        recyclerViewLike = view.findViewById(R.id.recyclerViewLike);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void likeRecyclerViewListUpdate(ArrayList<MusicData> arrayList){

        // 어댑터에 데이터리스트 세팅
        adapter_like.setMusicData(arrayList);

        // recyclerView에 어댑터 세팅
        recyclerViewLike.setAdapter(adapter_like);
        adapter_like.notifyDataSetChanged();


    }

    public ArrayList<MusicData> getMusicList_like() {
        return musicList_like;
    }

    public MusicAdapter getAdapter_like() {
        return adapter_like;
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
