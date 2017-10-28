package com.avfplayer.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avfplayer.R;
import com.avfplayer.fragments.FragmentAllVideo;
import com.avfplayer.interfaces.OnVideoClick;
import com.avfplayer.models.SongDetail;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by softradix on 09/10/17.
 */

public class AdapterVideoSuggestions extends RecyclerView.Adapter<AdapterVideoSuggestions.RecyclerViewHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<SongDetail> playlist;
    OnVideoClick onVideoClick;

    public AdapterVideoSuggestions(Context context, ArrayList<SongDetail> playlist, OnVideoClick onVideoClick) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.playlist = playlist;
        this.onVideoClick = onVideoClick;
    }

    @Override
    public AdapterVideoSuggestions.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.adapter_video_suggestions, parent, false);
        AdapterVideoSuggestions.RecyclerViewHolder viewHolder = new AdapterVideoSuggestions.RecyclerViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterVideoSuggestions.RecyclerViewHolder holder, final int position) {
        holder.videoName.setText(playlist.get(position).getDisplay_name());

        holder.videoLength.setText(FragmentAllVideo.getDurationBreakdown
                (TimeUnit.SECONDS.toMillis(Long.parseLong(playlist.get(position).getDuration()))));

        Glide.with(context).load(playlist.get(position).getPath().toString())
                .thumbnail(0.5f)
                .placeholder(R.drawable.bg_default_album_art)
                .into(holder.videoThumbnail);

        /*Glide.with(context)
                .load(playlist.get(position).getPath().toString())
                // use dontAnimate and not crossFade to avoid a bug with custom views
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        // do something
                        Log.d("onException", "onException");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource,
                                                   String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.videoLay.setVisibility(View.VISIBLE);
                        // do something
                        Log.d("onResourceReady", "onResourceReady");
                        return false;
                    }
                })
                .into(holder.videoThumbnail);*/

        holder.videoLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onVideoClick.onVideoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView videoName, videoLength;
        ImageView videoThumbnail;
        RelativeLayout videoLay;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            videoName = (TextView) itemView.findViewById(R.id.video_name);
            videoLength = (TextView) itemView.findViewById(R.id.video_length);
            videoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            videoLay = (RelativeLayout) itemView.findViewById(R.id.video_lay);

        }
    }

}