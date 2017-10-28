package com.avfplayer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avfplayer.R;
import com.avfplayer.adapter.AdapterAudioVideo;


public class FragmentAllAudioVideoSong extends Fragment {

    private View view;
    private RecyclerView rv_AudioVideo;
    private LinearLayoutManager layoutManager;

    public FragmentAllAudioVideoSong() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_fragment_all_audio_video_song, container, false);

        rv_AudioVideo= (RecyclerView) view.findViewById(R.id.rv_AudioVideo);

        layoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_AudioVideo.setLayoutManager(layoutManager);

        AdapterAudioVideo adapter=new AdapterAudioVideo(getActivity());
        rv_AudioVideo.setAdapter(adapter);


        return view;
    }
}
