package com.alutiiqlanguageproject.alutiiqdictionary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Based off of an SQLiteOpenHelper class, this sets up all of the structure of the
 * database as well as allows one to update the database.  Upon updating, one must increment
 * the DATABASE_VERSION to the next highest number.
 */
public class WordDBOpenHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "alutiiqDictionary.db"; //database name in the phone
	private static final int DATABASE_VERSION = 4;

	//all of these values will be used to instantiate word objects
	public static final String DICTIONARY_NAME = "dictionary";
	public static final String COLUMN_ID = "wordId";
	public static final String COLUMN_ENGLISH = "english";
	public static final String COLUMN_ALUNORTH = "alutiiqNorth";
	public static final String COLUMN_ALUSOUTH = "alutiiqSouth";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_FUNCTION = "function";
	public static final String COLUMN_COMMENTS = "comments";

	//all of these will used to instantiate vocabList objects
	public static final String LISTS_NAME = "vocabLists";
	public static final String VOCAB_LISTS = "listIds";
	public static final String VOCAB_LIST_NAMES = "listNames";
	public static final String VOCAB_WORD_IDS = "vocabIds";
	
	private static final String LOGTAG = "alutiiqDictionary";

	//This creates the table which holds all of the words
	private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE " + DICTIONARY_NAME +
			" (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ENGLISH +
			" TEXT, " + COLUMN_ALUNORTH + " TEXT, " + COLUMN_ALUSOUTH + " TEXT, " +
			COLUMN_CATEGORY + " TEXT, " + COLUMN_FUNCTION + 
			" TEXT, " + COLUMN_COMMENTS + " TEXT" + ")";

	//this creates the table which holds all of the vocabIds
	private static final String VOCAB_LIST_CREATE = "CREATE TABLE " + LISTS_NAME + " (" + VOCAB_LISTS +
			" INTEGER PRIMARY KEY AUTOINCREMENT, " + VOCAB_LIST_NAMES + " TEXT, " + VOCAB_WORD_IDS + " TEXT)";

	//constructor for this object.
	public WordDBOpenHelper(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * this allows for the initial creation of the database
	 * @param db
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(DICTIONARY_TABLE_CREATE);
		db.execSQL(VOCAB_LIST_CREATE);
		
		Log.i(LOGTAG, "Tables created");

	}

	/**
	 * Wrapper Allows for the database to be upgraded.  Is automatically checked upon opening the app
	 * @param db - database to be upgraded
	 * @param oldVersion -old version
	 * @param newVersion - new version
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + DICTIONARY_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + LISTS_NAME);
		onCreate(db);
		
		Log.i(LOGTAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
		
	}

}
