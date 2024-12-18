package com.example.mymusicplayer.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.adapter.CarouselAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewMusicFragment extends Fragment {

    private ViewPager2 viewPager;
    private CarouselAdapter carouselAdapter;
    private List<Integer> imageList;

    public NewMusicFragment() {
        // Required empty public constructor
    }

    public static NewMusicFragment newInstance() {
        NewMusicFragment fragment = new NewMusicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_music, container, false);
        // 初始化轮播图
        initCarousel(view);
        return view;
    }

    // 初始化轮播图
    private void initCarousel(View view) {
        viewPager = view.findViewById(R.id.viewpager_banner);
        // 初始化图片数据
        imageList = new ArrayList<>();
        imageList.add(R.drawable.image1); // 替换为实际的图片资源
        imageList.add(R.drawable.image2);
        imageList.add(R.drawable.image3);

        // 设置适配器
        carouselAdapter = new CarouselAdapter(getContext(), imageList);
        viewPager.setAdapter(carouselAdapter);

        // 自动滚动功能
        autoScrollViewPager();
    }

    private void autoScrollViewPager() {
        viewPager.postDelayed(() -> {
            int currentItem = viewPager.getCurrentItem();
            int nextItem = (currentItem + 1) % imageList.size(); // 无限循环
            viewPager.setCurrentItem(nextItem, true);
            autoScrollViewPager();
        }, 3000); // 每3秒滚动一次
    }
}