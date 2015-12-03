package com.alutiiqlanguageproject.alutiiqdictionary.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class VocabList implements Parcelable {
	
	
	private long listId; //the number id of the list
	private String wordIds; //the individual IDs of the words, in comma-separated-String form because of the database
	private List<String> wordIdsArray; //contains an array of IDs that are finally separated
	
	private static final String LOGTAG = "alutiiqDictionary";

	public VocabList() {

	}

	/**
	 * creates the object from a Parcel, usually upon being sent to a new activity
	 * @param in - the object sent between activities
	 */
	public VocabList(Parcel in) {
		Log.i(LOGTAG, "list parceled");
		
		listId = in.readLong(); //gets id of the list
		wordIds = in.readString(); //gets the string
		//separates the wordIDs into a list of string-numbers
		for (int i = 0; i < wordIdsArray.size(); i++) {
			wordIdsArray.add(wordIds.substring(i, i+1));
		}
	}

	/**
	 * getters and setters
	 * @return
	 */
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

		return 0;
	}

	/**
	 * allows the object to be packaged up into a parcel and sent
	 * between activities
	 * @param dest
	 * @param flags
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {

		Log.i(LOGTAG, "List to Parcel");
		
		dest.writeLong(listId);
		dest.writeString(wordIds);
		
	}

}
