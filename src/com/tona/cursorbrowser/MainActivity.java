package com.tona.cursorbrowser;

import java.io.File;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.widget.EditText;
import android.widget.Toast;
public class MainActivity extends FragmentActivity {

	private CustomWebViewFragment fragment;

	// デフォルトのHP
	public static final String DEFAULT_HOME = "https://www.google.co.jp/";

	// 画像・キャッシュを保存する際のパス
	public static final String ROOTPATH = Environment.getExternalStorageDirectory().getPath() + "/CursorBrowser/";

	// 他クラスで使用する際のMainActivity変数
	private MainActivity main;

	private SharedPreferences pref;

	public HistorySaver historySaver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		setLanguage();
		setContentView(R.layout.activity_main);
		pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		main = this;

		if (pref.getBoolean("versionInitialize", true)) {
			File historyFile = new File(ROOTPATH + "HistorySaver");
			if (historyFile.exists()) {
				historyFile.delete();
				pref.edit().putBoolean("versionInitialize", false).commit();
			}
		}

		historySaver = new HistorySaver();
		String lastUrl;
		if (historySaver.restoreFromFile()) {
			lastUrl = historySaver.getCurrentURL();
			historySaver.setNotMove(true);
		} else {
			lastUrl = pref.getString("homepage", DEFAULT_HOME);
		}

		fragment = new CustomWebViewFragment(this, lastUrl);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.root, fragment, "fragment");
		transaction.commit();
	}

	private void setLanguage() {
		Locale locale = Locale.getDefault(); // アプリで使用されているロケール情報を取得
		if (locale.equals(Locale.JAPAN)) {
			locale = Locale.JAPAN;
		} else {
			locale = Locale.US;
		}
		Locale.setDefault(locale); // 新しいロケールを設定
		Configuration config = new Configuration();
		config.locale = locale; // Resourcesに対するロケールを設定
		Resources resources = getBaseContext().getResources();
		resources.updateConfiguration(config, null);
	}
	/**
	 * メニューの作成
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/**
	 * メニューがクリックされたときの処理の振り分け
	 *
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.bookmark :
//				Log.d("nd", "" + pref.getBoolean("bookmark_dialog", true));
//				if (pref.getBoolean("bookmark_dialog", true)) {
//					AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);
//					alertDlg.setTitle(getString(R.string.comfirm));
//					alertDlg.setMessage(getString(R.string.select_app));
//					alertDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							pref.edit().putBoolean("bookmark_dialog", false).commit();
//							Intent intent2 = new Intent(Intent.ACTION_CREATE_SHORTCUT);
//							startActivityForResult(intent2, 0);
//						}
//					});
//					alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//						}
//					});
//					alertDlg.show();
//				} else {
//					Intent intent2 = new Intent(Intent.ACTION_CREATE_SHORTCUT);
//					startActivityForResult(intent2, 0);
//				}
				Intent intent2 = new Intent();
				intent2.setAction(Intent.ACTION_CREATE_SHORTCUT);
				startActivityForResult(intent2, 0);
				break;
			case R.id.bookmark_add :
				 Intent intent = new Intent(Intent.ACTION_INSERT,android.provider.Browser.BOOKMARKS_URI);
				 intent.putExtra("title", fragment.getWebView().getTitle());
				 intent.putExtra("url", fragment.getWebView().getUrl());
				 startActivity(intent);
				break;
			case R.id.next :
				if (historySaver.canGoNext()) {
					historySaver.next();
					historySaver.setNotMove(true);
					try {
						WebBackForwardList list = fragment.getWebView().copyBackForwardList();
						String url = list.getItemAtIndex(list.getCurrentIndex() + 1).getUrl();
						if (fragment.getWebView().canGoBack() && url.equals(historySaver.getCurrentURL())) {
							fragment.getWebView().goForward();
						} else {
							fragment.getWebView().loadUrl(historySaver.getCurrentURL());
						}
					} catch (NullPointerException e) {
						fragment.getWebView().loadUrl(historySaver.getCurrentURL());
					}
				}
				break;
			case R.id.reload :
				historySaver.setNotMove(true);
				fragment.getWebView().reload();
				break;
			case R.id.general_settings :
				startActivity(new Intent(getApplicationContext(), GeneralPref.class));
				break;
			case R.id.cursor_settings :
				startActivity(new Intent(getApplicationContext(), Pref.class));
				break;
			case R.id.url_bar :
				final EditText e = fragment.getEditForm();
				e.setVisibility(View.VISIBLE);
				e.requestFocus();
				e.setSelection(0, e.getText().length());
				// 遅らせてフォーカスがセットされるのを待つ
				main.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Timer t = new Timer();
						t.schedule(new TimerTask() {
							@Override
							public void run() {
								InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								inputMethodManager.showSoftInput(e, InputMethodManager.SHOW_IMPLICIT);
							}
						}, 200);
					}
				});
				break;
			default :
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * ブックマークを呼び出して、選択した項目のURLを取得し表示する
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
			String uri = intent.getDataString();
			if (uri.startsWith("http://") || uri.startsWith("https://")) {
				fragment.getWebView().loadUrl(uri);
			} else {
				Toast.makeText(main, getString(R.string.invalid_url), Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * バックボタンが押下された時の挙動
	 */
	@Override
	public void onBackPressed() {
		if (historySaver.canGoBack()) {
			historySaver.back();
			historySaver.setNotMove(true);
			try {
				WebBackForwardList list = fragment.getWebView().copyBackForwardList();
				String url = list.getItemAtIndex(list.getCurrentIndex() - 1).getUrl();
				if (fragment.getWebView().canGoBack() && url.equals(historySaver.getCurrentURL())) {
					fragment.getWebView().goBack();
				} else {
					fragment.getWebView().loadUrl(historySaver.getCurrentURL());
				}
			} catch (NullPointerException e) {
				fragment.getWebView().loadUrl(historySaver.getCurrentURL());
			}
		} else {
			historySaver.deleteFile();
			finish();
		}
		return;
	}
}
