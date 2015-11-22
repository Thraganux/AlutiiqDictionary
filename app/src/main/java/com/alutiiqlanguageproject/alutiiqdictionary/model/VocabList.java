package com.alutiiqlanguageproject.alutiiqdictionary.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class VocabList implements Parcelable {
	
	
	private long listId;
	private String wordIds;
	private List<String> wordIdsArray;
	
	private static final String LOGTAG = "alutiiqDictionary";

	public VocabList() {
		// TODO Auto-generated constructor stub
	}
	
	public VocabList(Parcel in) {
		Log.i(LOGTAG, "list parceled");
		
		listId = in.readLong();
		wordIds = in.readString();
		for (int i = 0; i < wordIdsArray.size(); i++) {
			wordIdsArray.add(wordIds.substring(i, i+1));
		}
	}
	
	public long getListId() {
		return listId;
	}

	public void setListId(long listId) {
		this.listId = listId;
	}

	public String getWordIds() {
		return wordIds;
	}

	public void setWordIds(String wordIds) {
		this.wordIds = wordIds;
	}

	public List<String> getWordIdsArray() {
		return wordIdsArray;
	}

	public void setWordIdsArray() {
		for (int i = 0; i < wordIdsArray.size(); i++) {
			wordIdsArray.add(wordIds.substring(i, i+1));
		}
		
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		Log.i(LOGTAG, "List to Parcel");
		
		dest.writeLong(listId);
		dest.writeString(wordIds);
		
	}

}
