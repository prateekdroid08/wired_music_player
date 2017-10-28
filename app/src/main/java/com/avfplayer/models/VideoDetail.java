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

public class VideoDetail {
	public int VIDEO_ID;
	public String VIDEO_URL;
	public String VIDEO_NAME;
	public String VIDEO_SIZE;
	public String VIDEO_DURATION;
	public long VIDEO_MILLI_SECONDS;
	public int VIDEO_PLAYED_TIME;
	public int PLAY_LIST_ID;

	public int getVIDEO_ID() {
		return VIDEO_ID;
	}

	public void setVIDEO_ID(int VIDEO_ID) {
		this.VIDEO_ID = VIDEO_ID;
	}

	public String getVIDEO_URL() {
		return VIDEO_URL;
	}

	public void setVIDEO_URL(String VIDEO_URL) {
		this.VIDEO_URL = VIDEO_URL;
	}

	public String getVIDEO_SIZE() {
		return VIDEO_SIZE;
	}

	public void setVIDEO_SIZE(String VIDEO_SIZE) {
		this.VIDEO_SIZE = VIDEO_SIZE;
	}

	public String getVIDEO_DURATION() {
		return VIDEO_DURATION;
	}

	public void setVIDEO_DURATION(String VIDEO_DURATION) {
		this.VIDEO_DURATION = VIDEO_DURATION;
	}

	public long getVIDEO_MILLI_SECONDS() {
		return VIDEO_MILLI_SECONDS;
	}

	public void setVIDEO_MILLI_SECONDS(long VIDEO_MILLI_SECONDS) {
		this.VIDEO_MILLI_SECONDS = VIDEO_MILLI_SECONDS;
	}

	public int getVIDEO_PLAYED_TIME() {
		return VIDEO_PLAYED_TIME;
	}

	public void setVIDEO_PLAYED_TIME(int VIDEO_PLAYED_TIME) {
		this.VIDEO_PLAYED_TIME = VIDEO_PLAYED_TIME;
	}

	public int getPLAY_LIST_ID() {
		return PLAY_LIST_ID;
	}

	public void setPLAY_LIST_ID(int PLAY_LIST_ID) {
		this.PLAY_LIST_ID = PLAY_LIST_ID;
	}

	public String getVIDEO_NAME() {
		return VIDEO_NAME;
	}

	public void setVIDEO_NAME(String VIDEO_NAME) {
		this.VIDEO_NAME = VIDEO_NAME;
	}
}
