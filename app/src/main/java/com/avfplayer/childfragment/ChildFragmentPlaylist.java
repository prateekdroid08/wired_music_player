package com.avfplayer.childfragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avfplayer.R;
import com.avfplayer.activities.AlbumAndArtisDetailsActivity;
import com.avfplayer.dbhandler.PlaylistTableHelper;
import com.avfplayer.phonemidea.PhoneMediaControl;
import com.avfplayer.utility.LogWriter;
import com.avfplayer.video.ActivityVideoPlayList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildFragmentPlaylist extends Fragment {

    private static final String TAG = "ChildFragmentPlaylist";
    private static Context context;
    private RecyclerView recyclerView;
    private PlaylistRecyclerAdapter mAdapter;
    private ArrayList<HashMap<String, String>> playlist = new ArrayList<>();

    public ChildFragmentPlaylist() {
        // Required empty public constructor
    }

    /*public static ChildFragmentPlaylist newInstance(int position, Context mContext) {
        ChildFragmentPlaylist f = new ChildFragmentPlaylist();
        context = mContext;
        return f;
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentchild_album, null);
        setupView(view);
        return view;
    }

    private void setupView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        populateData();
    }

    private void populateData() {
        mAdapter = (PlaylistRecyclerAdapter) getActivity().getLastNonConfigurationInstance();
        if (mAdapter == null) {
            mAdapter = new PlaylistRecyclerAdapter(getActivity(), PlaylistTableHelper.getInstance(getActivity()).getPlayListFromSQLDBCursor());
            recyclerView.setAdapter(mAdapter);
        } else {
            recyclerView.setAdapter(mAdapter);

        }
    }

    public class PlaylistRecyclerAdapter extends RecyclerView.Adapter<PlaylistRecyclerAdapter.ViewHolder> {

        ArrayList<HashMap<String, String>> list;

        public PlaylistRecyclerAdapter(Context context, ArrayList<HashMap<String, String>> list) {

            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int position) {
            View view = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.inflate_grid_item, viewgroup, false);

            ViewHolder holder = new ViewHolder(view);
            holder.line1 = (TextView) view.findViewById(R.id.line1);
            holder.line2 = (TextView) view.findViewById(R.id.line2);
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.view = view;
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.line1.setText(list.get(position).get("playlistName"));
            holder.line2.setVisibility(View.GONE);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        if (list.get(position).get("type").equalsIgnoreCase("video")) {
                            startActivity(new Intent(getActivity(), ActivityVideoPlayList.class).putExtra("playlistId",
                                    Integer.parseInt(list.get(position).get("playlistId"))));
                        } else {
                            Log.e("click id", "aa" + position);
                            Intent mIntent = new Intent(getActivity(), AlbumAndArtisDetailsActivity.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putLong("id", Integer.parseInt(list.get(position).get("playlistId")));
                            mBundle.putLong("tagfor", PhoneMediaControl.SonLoadFor.Playlist.ordinal());
                            mBundle.putString("albumname", holder.line1.getText().toString().trim());
                            mBundle.putString("title_one", "All my songs");
                            mBundle.putString("title_sec", holder.line2.getText().toString().trim());
                            mIntent.putExtras(mBundle);
                            getActivity().startActivity(mIntent);
                            ((Activity) getActivity()).overridePendingTransition(0, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogWriter.info(TAG, e.toString());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private long getGenerID(int position) {
            return getItemId(position);
        }


        protected class ViewHolder extends RecyclerView.ViewHolder {
            TextView line1;
            TextView line2;
            ImageView icon;
            View view;

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
