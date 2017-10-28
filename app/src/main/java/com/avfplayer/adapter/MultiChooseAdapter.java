package com.avfplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avfplayer.R;
import com.avfplayer.global.MultiChooseInfo;
import com.avfplayer.models.SongDetail;
import com.avfplayer.phonemidea.AVFPlayerUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by VlogicLabs on 3/25/2017.
 */

public class MultiChooseAdapter extends BaseAdapter {
    private Context context = null;
    private LayoutInflater layoutInflater;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    ArrayList<SongDetail> songList;
    private ArrayList<Boolean> selectedList;
    MultiChooseInfo info;


    public MultiChooseAdapter(Context mContext, ArrayList<SongDetail> songList) {
        this.context = mContext;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        this.songList=songList;
        selectedList=new ArrayList<>();
        info=new MultiChooseInfo();

        for(int i=0;i<songList.size(); i++) {
            selectedList.add(false);


        }

        info.setSelectedList(selectedList);



    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.multi_choose_item_layout, null);
            mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.inflate_allsong_row);
            mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongname);
            mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
            mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.inflate_allsong_imgSongThumb);
            //mViewHolder.imagemore = (CheckBox) convertView.findViewById(R.id.img_moreicon);
            //mViewHolder.select= (CheckBox) convertView.findViewById(R.id.img_moreicon);

            //mViewHolder.select.setTag(position);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        SongDetail mDetail = songList.get(position);

        String audioDuration = "";

        try {
            audioDuration = AVFPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mViewHolder.textViewSongArtisNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
        mViewHolder.textViewSongName.setText(mDetail.getTitle());
        final String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
        imageLoader.displayImage(contentURI, mViewHolder.imageSongThm, options);

        if(info.getSelectedList().get(position).equals(false)) {
            //mViewHolder.select.setSelected(false);
            convertView.setBackgroundColor(Color.parseColor("#e9e9e9"));
            Log.e("selectedBoolean1", ""+selectedList.get(position)+"//"+position);
        } else {

            //mViewHolder.select.setSelected(true);
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            Log.e("selectedBoolean2", ""+selectedList.get(position)+"//"+position);
        }


    convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //boolean check=mViewHolder.select.isChecked();
                //Log.e("checkBoolean", ""+check+"//"+position);

                if(!info.getSelectedList().get(position)) {
                    info.getSelectedList().set(position, true);
                    //mViewHolder.select.setSelected(true);

                } else {
                    info.getSelectedList().set(position, false);
                    //mViewHolder.select.setSelected(false);
                }

                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    @Override
    public int getCount() {
        return (songList != null) ? songList.size() : 0;
    }

    class ViewHolder {
        TextView textViewSongName;
        ImageView imageSongThm, imagemore;
        TextView textViewSongArtisNameAndDuration;
        //CheckBox select;
        LinearLayout song_row;
    }
}
