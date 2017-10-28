package com.avfplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avfplayer.R;
import com.avfplayer.activities.HomeActivity;
import com.avfplayer.global.Global;
import com.avfplayer.manager.MediaController;
import com.avfplayer.manager.MusicPreferance;
import com.avfplayer.models.SongDetail;
import com.avfplayer.phonemidea.AVFPlayerUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by VlogicLabs on 1/21/2017.
 */

public class SearchAdapter extends ArrayAdapter implements Filterable {

    Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ArrayList<SongDetail> songList;
    private DisplayImageOptions options;
    Global global;
    ArrayList<SongDetail> songList2=new ArrayList<>();
    ArrayList<Integer> pos=new ArrayList<>();

    public SearchAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.context=context;
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        global=new Global();
        songList= MusicPreferance.getPlaylist(context);
    }


    @Override
    public int getCount() {
        return songList2.size();
    }

    @Override
    public Object getItem(int index) {
        return songList2.get(index);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try{

            ViewHolder mViewHolder = new ViewHolder();
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.inflate_allsongsitem, parent, false);

            mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.inflate_allsong_row);
            mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongname);
            mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
            mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.inflate_allsong_imgSongThumb);
            mViewHolder.imagemore = (ImageView) convertView.findViewById(R.id.img_moreicon);

            songList= MusicPreferance.getPlaylist(context);
            SongDetail mDetail = songList.get(pos.get(position));

            String audioDuration = "";
            try {
                audioDuration = AVFPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            mViewHolder.textViewSongArtisNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
            mViewHolder.textViewSongName.setText(mDetail.getTitle());
            String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
            imageLoader.displayImage(contentURI, mViewHolder.imageSongThm, options);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongDetail mDetail = songList.get(pos.get(position));

                    ((HomeActivity)context).loadSongsDetails(mDetail);

                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().playAudio(songList.get(pos.get(position)));
                            MediaController.getInstance().currentPlaylistNum=pos.get(position);
                        }
                    }
                }
            });



        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    int count = songList.size();

                    songList2.clear();
                    pos.clear();

                    for (int i = 0; i < count; i++) {
                        if (songList.get(i).getDisplay_name().toLowerCase().contains(filterString)) {
                            pos.add(i);
                            songList2.add(songList.get(i));
                            notifyDataSetChanged();
                        }
                    }

                    filterResults.values = songList2;
                    filterResults.count = songList2.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    class ViewHolder {
        TextView textViewSongName;
        ImageView imageSongThm, imagemore;
        TextView textViewSongArtisNameAndDuration;
        LinearLayout song_row;
    }
}
