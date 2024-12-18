package com.example.mymusicplayer.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.adapter.SongListAdapter;
import com.example.mymusicplayer.data.model.SongList;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongListFragment extends Fragment {

    private RecyclerView recyclerView;
    private SongListAdapter adapter;
    private List<SongList> songLists;

    public SongListFragment() {
        // Required empty public constructor
    }

    public static SongListFragment newInstance() {
        SongListFragment fragment = new SongListFragment();
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
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_song_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // 初始化数据
        initSongLists();

        // 设置适配器
        adapter = new SongListAdapter(songLists);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void initSongLists() {
        songLists = new ArrayList<>();
        // todo 动态数据
        songLists.add(new SongList("实力女团 活力燃歌", 5, R.drawable.ic_album_cover));
        songLists.add(new SongList("那些温暖过我们的声音", 2, R.drawable.ic_album_cover2));
        songLists.add(new SongList("沉浸的老歌 让时空有了交集", 6, R.drawable.ic_album_cover3));
        songLists.add(new SongList("怀旧金曲", 2, R.drawable.ic_album_cover3));
        songLists.add(new SongList("心动情歌", 3, R.drawable.ic_album_cover2));
        songLists.add(new SongList("年度热歌榜", 9, R.drawable.ic_album_cover));
    }
}