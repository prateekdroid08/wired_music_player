/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.avfplayer.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SongDetail {
	public int id;
	public int album_id;
	public String artist;
	public String title;
	public String display_name;
	public String duration;
	public String path;
	public float audioProgress = 0.0f;
	public int audioProgressSec = 0;
	public int type;
	public int videoCheck;

	public SongDetail(int _id, int aLBUM_ID, String _artist, String _title, String _path, String _display_name, String _duration, int type, int videoCheck) {
		this.id = _id;
		this.album_id = aLBUM_ID;
		this.artist = _artist;
		this.title = _title;
		this.path = _path;
		this.display_name = _display_name;
		this.duration = TextUtils.isEmpty(_duration) ? "0" : String.valueOf((Long.valueOf(_duration) / 1000));
		this.type=type;
		this.videoCheck=videoCheck;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(int album_id) {
		this.album_id = album_id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setType(int type) {
		this.type=type;
	}

	public int getType() {
		return type;
	}

	public void setVideoCheck(int videoCheck) {
		this.videoCheck=videoCheck;
	}

	public int getVideoCheck() {
		return videoCheck;
	}

	public Bitmap getSmallCover(Context context) {

		// ImageLoader.getInstance().getDiskCache().g
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap curThumb = null;
		Uri uri=null;
		try {
			if (getDisplay_name().endsWith(".mp3")) {
				uri = Uri.parse("content://media/external/audio/media/" + getId() + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					curThumb = BitmapFactory.decodeFileDescriptor(fd);
				}
			} else {
				Log.e("Video Path", getPath());
				//options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				curThumb = getBitmap();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return curThumb;
	}

	public Bitmap getCover(Context context) {

		// ImageLoader.getInstance().getDiskCache().g
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap curThumb = null;
		Uri uri=null;
		try {
			if (getDisplay_name().endsWith(".mp3")) {
				uri = Uri.parse("content://media/external/audio/media/" + getId() + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					curThumb = BitmapFactory.decodeFileDescriptor(fd);
				}
			} else {
				//options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				curThumb = getBitmap();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return curThumb;
	}

	private Bitmap getBitmap() {
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(getPath(), MediaStore.Images.Thumbnails.MINI_KIND);

		return bitmap;
	}
}
