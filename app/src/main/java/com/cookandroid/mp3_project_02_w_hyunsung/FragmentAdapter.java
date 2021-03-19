package com.cookandroid.mp3_project_02_w_hyunsung;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    //보여줄 프래그먼트 갯수 선정
    private int count;

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity, int count) {
        super(fragmentActivity);
        this.count = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = position % count;

//        switch(index) {
//            case 0 :
//                return Fragment01.newInstance(index +1);
//            case 1 :
//                return Fragment02.newInstance(index +1);
//            case 2 :
//                return Fragment03.newInstance(index +1);
//            case 3 :
//                return Fragment04.newInstance(index +1);
//            default:
//                 Log.e("FragmentAdapter : ", "FragmentAdapter 에러발생");
//        }
        if(index ==0) {
            return Fragment_Music.newInstance(index +1);
        } else if(index ==1) {
            return Fragment_Like.newInstance(index +1);
        } else {
            return Fragment_Album.newInstance(index +1);
        }
    }

    @Override
    public int getItemCount() {
        return 200;
    }
}
