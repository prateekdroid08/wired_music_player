/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.avfplayer.fragments;

import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.avfplayer.R;
import com.avfplayer.uicomponent.VerticalSeekBar;

public class FragmentEqualizer extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

	TextView bass_boost_label = null;
	SeekBar bass_boost = null;
	CheckBox enabled = null;
	Button flat = null;

	Equalizer eq = null;
	BassBoost bb = null;

	int min_level = 0;
	int max_level = 100;

	static final int MAX_SLIDERS = 8; // Must match the XML layout
	VerticalSeekBar sliders[] = new VerticalSeekBar[MAX_SLIDERS];
	TextView slider_labels[] = new TextView[MAX_SLIDERS];
	int num_sliders = 0;

	public FragmentEqualizer() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.fragment_equalizer, null);
		setupInitialViews(rootview);
		return rootview;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void setupInitialViews(View inflatreView) {
		enabled = (CheckBox)inflatreView.findViewById(R.id.enabled);
		enabled.setOnCheckedChangeListener (this);

		flat = (Button)inflatreView.findViewById(R.id.flat);
		flat.setOnClickListener(this);

		bass_boost = (SeekBar)inflatreView.findViewById(R.id.bass_boost);
		bass_boost.setOnSeekBarChangeListener(this);
		bass_boost_label = (TextView) inflatreView.findViewById (R.id.bass_boost_label);

		sliders[0] = (VerticalSeekBar) inflatreView.findViewById(R.id.slider_1);
		slider_labels[0] = (TextView)inflatreView.findViewById(R.id.slider_label_1);
		sliders[1] = (VerticalSeekBar) inflatreView.findViewById(R.id.slider_2);
		slider_labels[1] = (TextView)inflatreView.findViewById(R.id.slider_label_2);
		sliders[2] = (VerticalSeekBar) inflatreView.findViewById(R.id.slider_3);
		slider_labels[2] = (TextView)inflatreView.findViewById(R.id.slider_label_3);
		sliders[3] = (VerticalSeekBar) inflatreView.findViewById(R.id.slider_4);
		slider_labels[3] = (TextView)inflatreView.findViewById(R.id.slider_label_4);
		sliders[4] = (VerticalSeekBar) inflatreView.findViewById(R.id.slider_5);
		slider_labels[4] = (TextView)inflatreView.findViewById(R.id.slider_label_5);
		sliders[5] = (VerticalSeekBar) inflatreView.findViewById(R.id.slider_6);
		slider_labels[5] = (TextView)inflatreView.findViewById(R.id.slider_label_6);
		sliders[6] = (VerticalSeekBar) inflatreView.findViewById(R.id.slider_7);
		slider_labels[6] = (TextView)inflatreView.findViewById(R.id.slider_label_7);
		sliders[7] = (VerticalSeekBar) inflatreView.findViewById(R.id.slider_8);
		slider_labels[7] = (TextView)inflatreView.findViewById(R.id.slider_label_8);

		eq = new Equalizer(0, 0);

		if (eq != null)
		{
			eq.setEnabled (true);
			int num_bands = eq.getNumberOfBands();
			num_sliders = num_bands;
			short r[] = eq.getBandLevelRange();
			min_level = r[0];
			max_level = r[1];
			for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
			{
				int[] freq_range = eq.getBandFreqRange((short)i);
				sliders[i].setOnSeekBarChangeListener(this);
				slider_labels[i].setText (formatBandLabel (freq_range));
			}
		}
		for (int i = num_sliders ; i < MAX_SLIDERS; i++)
		{
			sliders[i].setVisibility(View.GONE);
			slider_labels[i].setVisibility(View.GONE);
		}

		bb = new BassBoost(0, 0);
		if (bb != null)
		{
		}
		else
		{
			bass_boost.setVisibility(View.GONE);
			bass_boost_label.setVisibility(View.GONE);
		}

		Log.e("EQ Band Name", eq.getPresetName((short) 0));
		Log.e("EQ Band Name", eq.getPresetName((short) 1));
		Log.e("EQ Band Name", eq.getPresetName((short) 2));
		Log.e("EQ Band Name", eq.getPresetName((short) 3));
		Log.e("EQ Band Name", eq.getPresetName((short) 4));
		Log.e("EQ Band Name", eq.getPresetName((short) 5));
		Log.e("EQ Band Name", eq.getPresetName((short) 6));
		Log.e("EQ Band Name", eq.getPresetName((short) 7));
		Log.e("EQ Band Name", eq.getPresetName((short) 8));
		Log.e("EQ Band Name", eq.getPresetName((short) 9));


		updateUI();
	}

	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
		if (compoundButton == (View) enabled)
		{
			eq.setEnabled (b);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == (View) flat)
		{
			setFlat();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
		if (seekBar == bass_boost)
		{
			bb.setEnabled (i > 0 ? true : false);
			bb.setStrength ((short)i); // Already in the right range 0-1000
		}
		else if (eq != null)
		{
			int new_level = min_level + (max_level - min_level) * i / 100;

			for (int j = 0; j < num_sliders; j++)
			{
				if (sliders[j] == seekBar)
				{
					eq.setBandLevel ((short)j, (short)new_level);
					break;
				}
			}
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	public String formatBandLabel (int[] band)
	{
		return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
	}

	public String milliHzToString (int milliHz)
	{
		if (milliHz < 1000) return "";
		if (milliHz < 1000000)
			return "" + (milliHz / 1000) + "Hz";
		else
			return "" + (milliHz / 1000000) + "kHz";
	}

	public void updateUI ()
	{
		updateSliders();
		updateBassBoost();
		enabled.setChecked (eq.getEnabled());
	}

	public void updateSliders ()
	{
		for (int i = 0; i < num_sliders; i++)
		{
			int level;
			if (eq != null)
				level = eq.getBandLevel ((short)i);
			else
				level = 0;
			int pos = 100 * level / (max_level - min_level) + 50;
			sliders[i].setProgress (pos);
		}
	}

	public void updateBassBoost ()
	{
		if (bb != null)
			bass_boost.setProgress (bb.getRoundedStrength());
		else
			bass_boost.setProgress (0);
	}

	public void setFlat ()
	{


		if (eq != null)
		{
			for (int i = 0; i < num_sliders; i++)
			{
				eq.setBandLevel ((short)i, (short)0);



			}
		}

		if (bb != null)
		{
			bb.setEnabled (false);
			bb.setStrength ((short)0);
		}

		updateUI();
	}
}
