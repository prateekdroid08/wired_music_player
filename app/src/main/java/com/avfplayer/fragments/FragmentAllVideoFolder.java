package com.avfplayer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avfplayer.R;
import com.avfplayer.adapter.VideoFolderAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllVideoFolder extends Fragment {

    View view;
    RecyclerView recycler_videoFolderList;
    ImageView back;

    public FragmentAllVideoFolder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_fragment_all_video_folder, container, false);

        back= (ImageView) getActivity().findViewById(R.id.menu_icon_image);
        back.setVisibility(View.GONE);

        recycler_videoFolderList = (RecyclerView) view.findViewById(R.id.recycler_allVideoFolder);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_videoFolderList.setLayoutManager(linearLayoutManager);


        VideoFolderAdapter videoFolderAdapter=new VideoFolderAdapter(getActivity());
        recycler_videoFolderList.setAdapter(videoFolderAdapter);

        return view;
    }

}
