package com.cookandroid.mp3_project_02_w_hyunsung;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Fragment_Search extends Fragment {

    private RecyclerView recyclerViewSearch;
    private EditText edtSearch;
    private Button btnSearch;

    private ArrayList<MusicData> searchList = new ArrayList<>();

    private MusicDBHelper dbHelper;
    private MusicAdapter adapter_search;
    private MainActivity mainActivity;
    private MainActivity mainActivity2;

    public static Fragment_Search newInstance(int fragNumber) {
        Fragment_Search fragment_album = new Fragment_Search();
        Bundle bundle = new Bundle();
        bundle.putInt("fragNumber", fragNumber);
        fragment_album.setArguments(bundle);
        return fragment_album;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
        if(context instanceof MainActivity) {
            this.mainActivity2 = (MainActivity)context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        dbHelper = MusicDBHelper.getInstance(getActivity());

        findViewByIdFunc(view);

        eventHandlerFunc();

        return view;
    }

    private void eventHandlerFunc() {
        btnSearch.setOnClickListener((View v) -> {
            searchList = dbHelper.searchMusicTbl(edtSearch.getText().toString());
            adapter_search = new MusicAdapter(getContext());

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

            adapter_search.setMusicData(searchList);

            recyclerViewSearch.setAdapter(adapter_search);
            recyclerViewSearch.setLayoutManager(layoutManager);

            adapter_search.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    mainActivity.setMusicData_search(searchList.get(pos));
                    mainActivity.setPlayerData(pos, 3);
                }
            });
        });
    }

    private void findViewByIdFunc(View view) {
        recyclerViewSearch = view.findViewById(R.id.recyclerViewSearch);
        edtSearch = view.findViewById(R.id.edtSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
    }

    public ArrayList<MusicData> getSearchList() {
        return searchList;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public interface OnApplySelectedListener {
        void searchMusicData(ArrayList<MusicData> musicList);
    }

}
