package com.cookandroid.mp3_project_02_w_hyunsung;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPager2Adapter extends FragmentStateAdapter {
    //보여줄 프래그먼트 갯수 선정
    private int count;

    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity, int count) {
        super(fragmentActivity);
        this.count = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = position % count;

        if(index ==1) {
            return Fragment_Music.newInstance(index +1);
        } else if(index ==2) {
            return Fragment_Like.newInstance(index +1);
        } else {
            return Fragment_Search.newInstance(index +1);
        }
    }

    @Override
    public int getItemCount() {
        return 200;
    }
}
