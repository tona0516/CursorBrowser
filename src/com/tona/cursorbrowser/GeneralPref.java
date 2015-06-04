package com.tona.cursorbrowser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class GeneralPref extends PreferenceActivity {
	/** Called when the activity is first created. */
	private static SharedPreferences sp;
	private static EditTextPreference home;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.general_pref);
		loadAdvertisements();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		home = (EditTextPreference) findPreference("homepage");
		home.setText(sp.getString("homepage", MainActivity.DEFAULT_HOME));
		home.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String text = (String) newValue;
				if (text.startsWith("http://") || text.startsWith("https://")) {
					sp.edit().putString("homepage", text).commit();
				} else {
					Toast.makeText(getApplicationContext(), "不正なURLです", Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
	}

	private void loadAdvertisements() {
		final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
		AdView adView = new AdView(this);
		adView.setAdUnitId("ca-app-pub-4176998183155624/6476838791"); // 注1
		adView.setAdSize(AdSize.BANNER);
		RelativeLayout layout_ad = new RelativeLayout(this);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(WC, WC);
		param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
		param.addRule(RelativeLayout.CENTER_HORIZONTAL, 2);
		layout_ad.addView(adView, param);
		addContentView(layout_ad, param);

		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}
}
