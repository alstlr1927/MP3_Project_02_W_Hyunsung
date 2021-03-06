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
        mainActivity = (MainActivity) getActivity();
        if (context instanceof MainActivity) {
            this.mainActivity2 = (MainActivity) context;
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
        //DB Helper 인스턴스
        dbHelper = MusicDBHelper.getInstance(getActivity());
        //id 설정
        findViewByIdFunc(view);
        //이벤트 설정
        eventHandlerFunc();

        return view;
    }

    private void eventHandlerFunc() {
        btnSearch.setOnClickListener((View v) -> {
            //select query 문으로 검색결과 ArrayList 에 담음
            searchList = dbHelper.searchMusicTbl(edtSearch.getText().toString());
            //어댑터 설정
            adapter_search = new MusicAdapter(getContext());
            //레이아웃 매니저 설정
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            //어댑터에 검색결과 삽입
            adapter_search.setMusicData(searchList);
            //리사이클러뷰에 어댑터 && 매니저 세팅
            recyclerViewSearch.setAdapter(adapter_search);
            recyclerViewSearch.setLayoutManager(layoutManager);
            //검색결과에 관한 클릭 이벤트
            adapter_search.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    //검색 음악 데이터 객체 전송
                    mainActivity.setMusicData_search(searchList.get(pos));
                    mainActivity.setPlayerData(pos, MainActivity.SEARCH);
                }
            });
        });
    }

    private void findViewByIdFunc(View view) {
        recyclerViewSearch = view.findViewById(R.id.recyclerViewSearch);
        edtSearch = view.findViewById(R.id.edtSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
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
