package com.tona.cursorbrowser;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class Pref extends PreferenceActivity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.pref);
		loadAdvertisements();
	}

	private void loadAdvertisements() {
		final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
		final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
		AdView adView = new AdView(this);
		adView.setAdUnitId("ca-app-pub-4176998183155624/6476838791"); // 注1
		adView.setAdSize(AdSize.BANNER);
		RelativeLayout layout_ad = new RelativeLayout(this);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(MP, WC);
		param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
		param.addRule(RelativeLayout.CENTER_HORIZONTAL, 2);
		layout_ad.addView(adView, param);
		addContentView(layout_ad, param);

		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}
}