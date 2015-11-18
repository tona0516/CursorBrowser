package com.tona.cursorbrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

import android.util.Log;

public class HistorySaver implements Serializable {
	public static final String TAG = "HistorySaver";

	private LinkedList<String> urlList;
	private int currentIndex;
	private boolean isNotMove;

	public HistorySaver() {
		urlList = new LinkedList<>();
		currentIndex = -1;
		isNotMove = false;
	}

	public void move(String url) {
		Log.d(TAG, "move");
		if (currentIndex != urlList.size() - 1) {
			int size = urlList.size();
			for (int i = currentIndex + 1; i < size; i++) {
				urlList.removeLast();
			}
			currentIndex = urlList.size() - 1;
		}
		urlList.add(url);
		currentIndex++;
		saveToFile();
	}

	public void back() {
		Log.d(TAG, "back");
		if (currentIndex - 1 >= 0) {
			currentIndex--;
			saveToFile();
		}
	}

	public boolean canGoBack() {
		if (currentIndex - 1 >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public void next() {
		Log.d(TAG, "next");
		if (currentIndex + 1 <= urlList.size() - 1) {
			currentIndex++;
			saveToFile();
		}
	}

	public boolean canGoNext() {
		if (currentIndex + 1 <= urlList.size() - 1) {
			return true;
		} else {
			return false;
		}
	}

	private boolean saveToFile() {
		Log.d(TAG, "index" + currentIndex);
		Log.d(TAG, "list" + urlList.toString());
		try {
			File ownFile = new File(MainActivity.ROOTPATH + TAG);
			if (!ownFile.getParentFile().exists())
				ownFile.getParentFile().mkdirs();
			ObjectOutputStream at_oos = new ObjectOutputStream(new FileOutputStream(ownFile));
			at_oos.writeObject(this);
			at_oos.flush();
			at_oos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean restoreFromFile() {
		try {
			File accessTokenFile = new File(MainActivity.ROOTPATH + TAG);
			ObjectInputStream at_ois = new ObjectInputStream(new FileInputStream(accessTokenFile));
			HistorySaver hs = (HistorySaver) at_ois.readObject();
			this.urlList = hs.getUrlList();
			this.currentIndex = hs.getCurrentIndex();
			at_ois.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void deleteFile() {
		File file = new File(MainActivity.ROOTPATH + TAG);
		if (file.exists()) {
			file.delete();
		}
	}

	public LinkedList<String> getUrlList() {
		return urlList;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public String getCurrentURL() {
		return urlList.get(currentIndex);
	}

	public boolean isNotMove() {
		return isNotMove;
	}

	public void setNotMove(boolean isNotMove) {
		this.isNotMove = isNotMove;
	}
}
