package com.alutiiqlanguageproject.alutiiqdictionary.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Word implements Parcelable {

	/*
	the following values are all taken from the database
	and used to instantiate the word object
	 */
	private long wordId;
	private String english;
	private String aluNorth;
	private String aluSouth;
	private String category;
	private String function;
	private String comments;
	
	private static final String LOGTAG = "alutiiqDictionary";
	
	public Word() {

	}

	/**
	 * creates the word object from a parcel, which has been sent from another activity
	 * @param in
	 */
	public Word(Parcel in){
		Log.i(LOGTAG, "Word Parceled");
		
		wordId = in.readLong();
		english = in.readString();
		aluNorth = in.readString();
		aluSouth = in.readString();
		category = in.readString();
		function = in.readString();
		comments = in.readString();
	}

	/****************************
	 * getters and setters for all the object fields
	 * @return
	 * ************************
	 */
	public long getWordId() {
		return wordId;
	}

	public void setWordId(long wordId) {
		this.wordId = wordId;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getAluNorth() {
		return aluNorth;
	}

	public void setAluNorth(String aluNorth) {
		this.aluNorth = aluNorth;
	}

	public String getAluSouth() {
		return aluSouth;
	}

	public void setAluSouth(String aluSouth) {
		this.aluSouth = aluSouth;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * this is a required function
	 * @return
	 */
	@Override
	public int describeContents() {

		return 0;
	}

	/**
	 * parcles the word in order to send it between activities
	 * @param dest
	 * @param flags
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		Log.i(LOGTAG, "Write to Parcel");
		
		dest.writeLong(wordId);
		dest.writeString(english);
		dest.writeString(aluNorth);
		dest.writeString(aluSouth);
		dest.writeString(category);
		dest.writeString(function);
		dest.writeString(comments);
		
	}

	/**
	 * creates the parcel
	 */
	public static final Creator<Word> CREATOR =
			new Creator<Word>() {

				@Override
				public Word createFromParcel(Parcel source) {
					// TODO Auto-generated method stub
					return new Word(source);
				}

				@Override
				public Word[] newArray(int size) {
					// TODO Auto-generated method stub
					return new Word[size];
				}
			};

}
