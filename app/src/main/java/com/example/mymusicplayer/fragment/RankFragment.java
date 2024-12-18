package com.example.mymusicplayer.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.adapter.RankAdapter;
import com.example.mymusicplayer.data.model.Song;
import com.example.mymusicplayer.service.MusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankFragment extends Fragment {

    private ListView lvSongList;
    private List<Song> songList;

    private MusicService musicService;

    public RankFragment() {
        // Required empty public constructor
    }

    public static RankFragment newInstance(MusicService musicService) {
        RankFragment fragment = new RankFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.musicService = musicService;
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
        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        lvSongList = view.findViewById(R.id.song_list);

        // 初始化歌曲数据
        songList = new ArrayList<>();
        songList.add(Song.songRank("单车", "陈奕迅", 1, R.raw.sample_rank1));
        songList.add(Song.songRank("不要说话", "陈奕迅", 2, R.raw.sample_rank2));
        songList.add(Song.songRank("虚位以待", "？", 3));
        songList.add(Song.songRank("虚位以待", "？", 4));
        songList.add(Song.songRank("虚位以待", "？", 5));

        // 设置Adapter
        RankAdapter adapter = new RankAdapter(getContext(), songList, musicService);
        lvSongList.setAdapter(adapter);

        return view;
    }
}