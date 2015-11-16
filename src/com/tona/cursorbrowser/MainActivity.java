package com.tona.cursorbrowser;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("LifeCycle", "OnCreate");
		super.onCreate(null);
		pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		setContentView(R.layout.activity_main);
		main = this;
		fragment = new CustomWebViewFragment(this, pref.getString("lastPage", null));
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.root, fragment, "fragment");
		transaction.commit();
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
			/*
			 * case R.id.set_homepage : AlertDialog.Builder alertDlg2 = new
			 * AlertDialog.Builder(MainActivity.this);
			 * alertDlg2.setTitle("以下のページをHPにしますか？");
			 * alertDlg2.setMessage(fragment.getWebView().getUrl());
			 * alertDlg2.setPositiveButton("Yes", new
			 * DialogInterface.OnClickListener() {
			 *
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { pref.edit().putString("homepage",
			 * fragment.getWebView().getUrl()).commit();
			 * Toast.makeText(getApplicationContext(), "このページをHPにしました",
			 * Toast.LENGTH_SHORT).show(); } });
			 * alertDlg2.setNegativeButton("No", new
			 * DialogInterface.OnClickListener() {
			 *
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { } }); alertDlg2.show(); break;
			 */
			case R.id.bookmark :
				Log.d("nd", "" + pref.getBoolean("bookmark_dialog", true));
				if (pref.getBoolean("bookmark_dialog", true)) {
					AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);
					alertDlg.setTitle("確認");
					alertDlg.setMessage("ブックマークのアプリケーションを選択してください");
					alertDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							pref.edit().putBoolean("bookmark_dialog", false).commit();
							Intent intent2 = new Intent(Intent.ACTION_CREATE_SHORTCUT);
							startActivityForResult(intent2, 0);
						}
					});
					alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alertDlg.show();
				} else {
					Intent intent2 = new Intent(Intent.ACTION_CREATE_SHORTCUT);
					startActivityForResult(intent2, 0);
				}
				break;
			case R.id.bookmark_add :
				Intent intent = new Intent(Intent.ACTION_INSERT, android.provider.Browser.BOOKMARKS_URI);
				intent.putExtra("title", fragment.getWebView().getTitle());
				intent.putExtra("url", fragment.getWebView().getUrl());
				startActivity(intent);
				break;
			case R.id.next :
				if (fragment.getWebView().canGoForward())
					fragment.getWebView().goForward();
				break;
			case R.id.reload :
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
			String uri = intent.toURI();
			if (uri.startsWith("http://") || uri.startsWith("https://")) {
				fragment.getWebView().loadUrl(uri);
			} else {
				Toast.makeText(main, "不正なURLです", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * バックボタンが押下された時の挙動
	 */
	@Override
	public void onBackPressed() {
		if (fragment.getWebView().canGoBack()) {
			fragment.getWebView().goBack();
		} else {
			finish();
		}
		return;
	}
}
