package com.alutiiqlanguageproject.alutiiqdictionary.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alutiiqlanguageproject.alutiiqdictionary.model.VocabList;
import com.alutiiqlanguageproject.alutiiqdictionary.model.Word;

/**
 * This class handles much of the database interaction, updating individual rows/entries/columns
 * as well as a few other functions such as search, sort, and etc
 */

public class WordsDataSource {

	// values to get the database
	SQLiteOpenHelper openHelper;
	SQLiteDatabase database;

	private static final String LOGTAG = "alutiiqDictionary";
	//a var to hold all of the column names; later used in a search function
	private static final String[] allColumns = {WordDBOpenHelper.COLUMN_ID, WordDBOpenHelper.COLUMN_ENGLISH,
		WordDBOpenHelper.COLUMN_ALUNORTH, WordDBOpenHelper.COLUMN_ALUSOUTH, WordDBOpenHelper.COLUMN_CATEGORY,
		WordDBOpenHelper.COLUMN_FUNCTION, WordDBOpenHelper.COLUMN_COMMENTS};
	//var to hold all of the vocablist column names, used for a search function
	private static final String[] allLists = {WordDBOpenHelper.VOCAB_LISTS, WordDBOpenHelper.VOCAB_LIST_NAMES, 
		WordDBOpenHelper.VOCAB_WORD_IDS};

	//constructor
	public WordsDataSource(Context context) {
		openHelper = new WordDBOpenHelper(context);
	}

	/**
	 * used to open the database  Needs to be opened again in each activity
	 */
	public void open() {
		Log.i(LOGTAG, "Database opened");
		database = openHelper.getWritableDatabase();
	}

	/**
	 * closes the database
	 */
	public void close() {
		Log.i(LOGTAG, "Database closed");
		openHelper.close();
	}

	/**
	 * This creates a word in the dictionary database
	 * @param word - word to be created
	 * @return - returns the word
	 */
	public Word create(Word word) {
		ContentValues values = new ContentValues();
		values.put(WordDBOpenHelper.COLUMN_ENGLISH, word.getEnglish());
		values.put(WordDBOpenHelper.COLUMN_ALUNORTH, word.getAluNorth());
		values.put(WordDBOpenHelper.COLUMN_ALUSOUTH, word.getAluSouth());
		values.put(WordDBOpenHelper.COLUMN_CATEGORY, word.getCategory());
		values.put(WordDBOpenHelper.COLUMN_FUNCTION, word.getFunction());
		values.put(WordDBOpenHelper.COLUMN_COMMENTS, word.getComments());
		long insertId = database.insert(WordDBOpenHelper.DICTIONARY_NAME, null, values);
		word.setWordId(insertId);
		return word;
	}

	/**
	 * takes in a cursor, applies it to the database sequentially, and then returns a list of words
	 * which fits the params in the cursor
	 * @param cursor - cursor with search parameters
	 * @return
	 */
	private List<Word> cursorToWordList(Cursor cursor) {
		List<Word> extracted = new ArrayList<Word>();
		if(cursor.getCount() > 0) { //checks that the cursor actually matched something
			while (cursor.moveToNext()) {
				//creates the word object
				Word word = new Word();
				word.setWordId(cursor.getLong(cursor.getColumnIndex(WordDBOpenHelper.COLUMN_ID)));
				word.setEnglish(cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.COLUMN_ENGLISH)));
				word.setAluNorth(cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.COLUMN_ALUNORTH)));
				word.setAluSouth(cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.COLUMN_ALUSOUTH)));
				word.setCategory(cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.COLUMN_CATEGORY)));
				word.setFunction(cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.COLUMN_FUNCTION)));
				word.setComments(cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.COLUMN_COMMENTS)));
				extracted.add(word); //adds word to list
			}
		}
		Log.i(LOGTAG, extracted.size() + " rows added to list in cursor to list");
		
		return extracted;
	}

    /**
     * takes a cursor, taakes the values from it and creates a vocab list
     * @param cursor -passed from a search function
     * @return
     */
	private List<VocabList> cursorToVocabLists(Cursor cursor) {
		List<VocabList> vocabLists = new ArrayList<VocabList>();
		if(cursor.getCount() > 0) {//checks that the cursor actually contains something
			while (cursor.moveToNext()) {
                //creates the vocab list object
				VocabList vocabList = new VocabList();
				vocabList.setListId(cursor.getLong(cursor.getColumnIndex(WordDBOpenHelper.VOCAB_LISTS)));
				vocabList.setWordIds(cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.VOCAB_WORD_IDS)));
				vocabList.setWordIdsArray();
				vocabLists.add(vocabList);	//adds vocablist to a lst
			}
		}
		return vocabLists;
	}

    /**Adds an empty vocab list to the vocabList database
     *
     * @param context - requires the activity context
     * @param name - new name for the database
     * @return
     */
	public boolean addVocabList(Context context, String name) {
		
		Log.i(LOGTAG, "add list entered");
        //creates a new value to be added to the database
		ContentValues values = new ContentValues();
		values.put(WordDBOpenHelper.VOCAB_LIST_NAMES, name);
		values.put(WordDBOpenHelper.VOCAB_WORD_IDS, "");//adds empty string for ID
        //inserts the values into the database
		long result = database.insert(WordDBOpenHelper.LISTS_NAME, null, values);
		if (result != -1) Log.i(LOGTAG, "list added");
		return (result != -1); //shows whether list was added
	}

    /**
     * removes vocab list from dictionary.
     * @param context - the activity context (usually "this")
     * @param name - name of the vocab list to be deleted
     * @return
     */
	public boolean removeVocabList(Context context, String name) {
		
		Log.i(LOGTAG, "delete list entered");
		long result = database.delete(WordDBOpenHelper.LISTS_NAME, 
				WordDBOpenHelper.VOCAB_LIST_NAMES + " = '" + name + "'" , null);
		return (result != -1);
	}

    /**
     * Adds word to a vocablist
     * @param context - context, usually activity context
     * @param fromRow -
     * @param word
     * @return
     */
	public boolean addWord(Context context, String fromRow, Word word) {
		//TODO allow adding word to either list or dictionary
		String wordIdQuery = "Select " + WordDBOpenHelper.VOCAB_WORD_IDS + " FROM " + 		
		WordDBOpenHelper.LISTS_NAME + " where " + WordDBOpenHelper.VOCAB_LIST_NAMES + " = '" + fromRow + "'";
		Log.i(LOGTAG, wordIdQuery);
		String inList = "";
		Cursor cursor = database.rawQuery(wordIdQuery, null);
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				inList = inList + cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.VOCAB_WORD_IDS));
			}
		}		
		inList = inList + Long.toString(word.getWordId()) +",";
		ContentValues values = new ContentValues();
		values.put(WordDBOpenHelper.VOCAB_WORD_IDS, inList);
		int result = database.update(WordDBOpenHelper.LISTS_NAME, values, WordDBOpenHelper.VOCAB_LIST_NAMES
				+ "='"+fromRow + "'", null );
		return (result != -1);
	}
	
	public boolean removeWord(Context context, String fromRow, Word word) {
		//TODO remove word from list or dictionary
		String wordIdQuery = "Select " + WordDBOpenHelper.VOCAB_WORD_IDS + " FROM " + 		
				WordDBOpenHelper.LISTS_NAME + " where " + WordDBOpenHelper.VOCAB_LIST_NAMES +
				" = '" + fromRow + "'";
		Log.i(LOGTAG, "in remove word " + wordIdQuery);
		String inList = "";
		Cursor cursor = database.rawQuery(wordIdQuery, null);
		if(cursor.getCount() > 0) {
			while (cursor.moveToNext()){
				inList = inList + cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.VOCAB_WORD_IDS));
			}
		}
		Log.i(LOGTAG, "inList = " + inList);
		String[] removeFrom = inList.split(",");
		String removeThis = Long.toString(word.getWordId());
		Log.i(LOGTAG, "removing " + removeThis);
		String result = "";
		int count = 0;
		for (String string : removeFrom) {
			Log.i(LOGTAG, "checking " + string);
			
			if (removeThis.equals(string) && count != 1) {
				count = count + 1;
			}
			else {
				result = result + string + ",";
			}
		}
	
		ContentValues values = new ContentValues();
		values.put(WordDBOpenHelper.VOCAB_WORD_IDS, result);
		Log.i(LOGTAG, "result of remove = " + result);
		int done = database.update(WordDBOpenHelper.LISTS_NAME, values, WordDBOpenHelper.VOCAB_LIST_NAMES
				+ "='"+fromRow + "'", null );
		return (done != -1);
		
	}
	public List<Word> searchWord(String search, String dataFilter, String searchLanguage){
		List<Word> found = new ArrayList<Word>();
		String query1 = "";
        String query2 ="";
		Log.i(LOGTAG, "Entering query");
		if (dataFilter == "All Words" ) {
		query1 = "SELECT * FROM " + WordDBOpenHelper.DICTIONARY_NAME + " WHERE (" +
				searchLanguage + " LIKE '"+ search + "%') ORDER BY " + searchLanguage + " COLLATE NOCASE ASC";
            query2 = "SELECT * FROM " + WordDBOpenHelper.DICTIONARY_NAME + " WHERE (" +
                    searchLanguage + " LIKE '%"+ search + "%') ORDER BY " + searchLanguage + " COLLATE NOCASE ASC";
		}
		else {
			query1 = "SELECT * FROM " + WordDBOpenHelper.DICTIONARY_NAME + " WHERE (" +
					searchLanguage + " LIKE '"+ search + "%' AND " + WordDBOpenHelper.COLUMN_FUNCTION + " = '" +
					dataFilter + "') ORDER BY " + searchLanguage + " COLLATE NOCASE ASC";
            query2 = "SELECT * FROM " + WordDBOpenHelper.DICTIONARY_NAME + " WHERE (" +
                    searchLanguage + " LIKE '%"+ search + "%' AND " + WordDBOpenHelper.COLUMN_FUNCTION + " = '" +
                    dataFilter + "') ORDER BY " + searchLanguage + " COLLATE NOCASE ASC";
		}
		Log.i(LOGTAG, query1);
		Cursor cursor = database.rawQuery(query1, null);
		found = cursorToWordList(cursor);
        Cursor cursor2 = database.rawQuery(query2, null);
        found.addAll(cursorToWordList(cursor2));
		return stripList(found);
	}

    /*
    ** Method to strip duplicate words (necessary due to
    * HashSet.contains method comparing by reference, not value)
     */
    private List<Word> stripList(List<Word> lt){
        ArrayList<Word> result = new ArrayList<Word>();
        HashSet<String> interim = new HashSet<String>();
        Log.i(LOGTAG, "size of strip list " + lt.size());
        for (Word word : lt) {
            if (!interim.contains(word.getEnglish())) {
                result.add(word);
                interim.add(word.getEnglish());
            }
        }
        Log.i(LOGTAG, "Size of result strip list " + result.size());
        return result;
    }

	public List<Word> findAll() {
		Cursor cursor = database.query(WordDBOpenHelper.DICTIONARY_NAME, allColumns, 
				null, null, null, null, null);
		
		Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");
		List<Word> allWords = cursorToWordList(cursor);

		return allWords;
	}
	
	public List<String> findAllList(){
		//TODO returns all the names of the vocab lists
		Log.i(LOGTAG, "HIT find all list");
		List<String> listNames = new ArrayList<String>();
		String rows[] = {WordDBOpenHelper.VOCAB_LIST_NAMES};
		Cursor cursor = database.rawQuery("Select " + WordDBOpenHelper.VOCAB_LIST_NAMES + " from " + WordDBOpenHelper.LISTS_NAME, null);
		if (cursor.getCount() != 0){
			while(cursor.moveToNext()) {
					String name =	cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.VOCAB_LIST_NAMES));
					Log.i(LOGTAG, name + " found");
						listNames.add(name);
			}
		}
		return listNames;
		
	}
	
	public List<Word> getVocabWords(String fromRow) {
		List<Word> names = new ArrayList<Word>();
		
		String wordIdQuery = "Select " + WordDBOpenHelper.VOCAB_WORD_IDS + " FROM " + 		
			WordDBOpenHelper.LISTS_NAME + " where " + WordDBOpenHelper.VOCAB_LIST_NAMES + " = '" + fromRow + "'";
			Log.i(LOGTAG, wordIdQuery);
			String inList ="";
			Cursor cursor = database.rawQuery(wordIdQuery, null);
			if(cursor.getCount() > 0){
				while(cursor.moveToNext()){
					inList = inList + cursor.getString(cursor.getColumnIndex(WordDBOpenHelper.VOCAB_WORD_IDS));
					Log.i(LOGTAG, inList.toString());
				}
			}	
			String[] newList = inList.split(",");
			Log.i(LOGTAG, newList.toString());
			if (newList.length != 0 && !newList[0].equals("") && newList != null) {
				Log.i(LOGTAG, "entering newlist");
			for (String string : newList) {
				Cursor newCursor = database.rawQuery("Select * From " + WordDBOpenHelper.DICTIONARY_NAME +
						" where " + WordDBOpenHelper.COLUMN_ID + " = " + string, null);
				names.addAll(cursorToWordList(newCursor));
				}
			}
		return names;
	}
	
	
	

}
