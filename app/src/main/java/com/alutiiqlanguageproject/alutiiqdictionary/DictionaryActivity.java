package com.alutiiqlanguageproject.alutiiqdictionary;

import java.lang.reflect.Field;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.alutiiqlanguageproject.alutiiqdictionary.db.WordDBOpenHelper;
import com.alutiiqlanguageproject.alutiiqdictionary.db.WordsDataSource;
import com.alutiiqlanguageproject.alutiiqdictionary.model.Word;
import com.alutiiqlanguageproject.alutiiqdictionary.xml.WordsPullParser;

public class DictionaryActivity extends ListActivity {

	// helps access the database and provides various functions to work with the database
	WordsDataSource datasource; 
	
	//checks if vocab is open for detailactivity
	private boolean isVocabOpen;  
	 // holds the list of dictionary entries; is changeable according to the search engine
	private List<Word> words; 
	//Checks whether the user wants to search english or alutiiq entries
	private boolean searchEnglish = true; 
	//provides filters for the searches
	private String[] searchFilters = {"All Words", "Noun", "Pronoun", "Adjective", "Adverb", "Verb"};
	//holds the current selected filter
	private String currentFilter;
	//allows the user to enter a query
	public EditText search;
	
	//when user decides to add word to file, this points to where
	private String selectedFile;
	
	//request codes
	private static final int DICTIONARY_ACTIVITY = 1001;
	private static final int VOCAB_LISTS_ACTIVITY = 1011;
	private static final int VOCAB_PRACTICE_ACTIVITY =1101;
	private static final int WORD_DETAIL_ACTIVITY = 1111;
	
	//LOGTAG
	private static final String LOGTAG = "alutiiqDict";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictionary);
		
		//opens and gets all the bundled info
		Bundle b = getIntent().getExtras();
		isVocabOpen = b.getBoolean("isVocabOpen");
		
		/* open database */
		datasource = new WordsDataSource(this);
		datasource.open();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
		
		//Opens the dictionary with a view of all the words in the dictionary. 
		//if the dictionary part of the database is empty, it adds the words from an xml file
		words = datasource.findAll();
		if (words.size() == 0) {
			createData();
			words = datasource.findAll();
		}
		isVocabOpen = false;

		//click sensors
        makeActionOverflowMenuShown();
		addOnWordLongClick ();
		addOnKeyEnter();
		addOnRadioClick();
		addOnFilterSelect();
		//refreshes display
		refreshDisplay();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dictionary, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch  (id) {
		
		//sends the user back to main screen
		case R.id.toMain:
			Intent intent = new Intent(DictionaryActivity.this, MainActivity.class);
			isVocabOpen = false;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);
            return true;

			
			//sends to view the vocab lists
		case R.id.toVocabLists:
			Intent intentV = new Intent(DictionaryActivity.this, VocabListActivity.class);
			isVocabOpen = true;
			intentV.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intentV, VOCAB_LISTS_ACTIVITY);
            return true;

		default:
            return super.onOptionsItemSelected(item);

		}

	}
	
	/*
	 * parses the xml file into a list of words, which is then added to the database via datasource.create(word)
	 */
	private void createData() {
		WordsPullParser parser = new WordsPullParser();
		List<Word> words = parser.parseXML(this);
		for (Word word : words) {
			datasource.create(word);
		}
	}
	
	/*
	 * refreshes the display in the dictionary activity after a search is initiated
	 * or etc.  Takes the list of words and sets a new adapter
	 */
	public void refreshDisplay(){
		Log.i(LOGTAG, "refreshed display in Dictionary Activity");
		
		ArrayAdapter<Word> adapter = new WordListAdapter(this, words);
		setListAdapter(adapter);
	}
	
	/*
	 * sets the edit text and listens for an enter key to initiate the search
	 */
	public void addOnKeyEnter(){
		
		//sets up edittext search bar
		search = (EditText) findViewById(R.id.searchField);

		//sets key listener for key entries in the edittext view
		search.setOnKeyListener(new OnKeyListener () {
			
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//checks that the enter key is the one that was pressed
				if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					//initiates a search for english entries in the database
					if (searchEnglish){
						//queries the database for any word like the entry and makes it the new word list
					words = datasource.searchWord(search.getText().toString(), currentFilter, 
							WordDBOpenHelper.COLUMN_ENGLISH);
					//Log.i(LOGTAG, "search started for: " + search.getText().toString());
						
						//informs the user that the search did go through, but found no entries
						if(words.size()== 0) {
							Toast noWords = Toast.makeText(DictionaryActivity.this, 
								"no words found for " + search.getText().toString(), Toast.LENGTH_LONG);
							noWords.show();
						}
						//refreshes display to show results of search
						refreshDisplay();
					}
					//Searches for words via the alutiiq entries, both north and south dialects
					else if (!searchEnglish) {
						//sets up sqlite query
						words = datasource.searchWord(search.getText().toString(), currentFilter, 
								WordDBOpenHelper.COLUMN_ALUNORTH + " like '%" + search.getText().toString() +
								"%' or "+ WordDBOpenHelper.COLUMN_ALUSOUTH);
						//Log.i(LOGTAG, "alutiiq search");
						//informs the user that the search did go through, but found no entries
						if(words.size()== 0) {
							Toast noWords = Toast.makeText(DictionaryActivity.this, 
									"no words found for " + search.getText().toString(), Toast.LENGTH_LONG);
							noWords.show();
						}
						//refreshes display to show results of search
						refreshDisplay();
					}
				}
				return false;
			}	
		});
		
	}
	
	/**
	 * Sets up the radio group to indicate whether 
	 * the user wants to search for alutiiq or english entries
	 * upon checking english, searchEnglish is set to true and so on
	 */
	public void addOnRadioClick(){
		
		//sets up radio group
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);
		final RadioButton engToAlu = (RadioButton) findViewById(R.id.englishToAlutiiq);
		final RadioButton aluToEng = (RadioButton) findViewById(R.id.alutiiqToEnglish);
		//sets checked listener
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//looks at which one was checked, changes searchEnglish value
				//and then changes the checked graphic to the RadioButton that
				//the user clicked on
				if(checkedId == R.id.englishToAlutiiq) {
					searchEnglish = true;
					engToAlu.setChecked(true);
					//Log.i(LOGTAG, "search for english entries");
				}
				else if(checkedId == R.id.alutiiqToEnglish) {
					searchEnglish = false;
					aluToEng.setChecked(true);
					//Log.i(LOGTAG, "search for alutiiq entries");
				}
				
			}
		});
	}
	
	/*
	 * sets up the filter spinner so that the user can filter search for
	 * selected grammar functions such as nouns, verbs, etc
	 */
	public void addOnFilterSelect() {
		
		//Log.i(LOGTAG, "adOnfilterselect");
		//sets up spinner with the searchFilters array
		Spinner filter = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_dropdown_item, searchFilters );
		filter.setAdapter(adapter);
		
		//sets up listener
		filter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//sets the current filter which is used only when the
				//user initiates a search from the edit text
				//by clicking enter key.
				// DO NOT initiate a search on its own
				currentFilter = searchFilters[position].toString();
				Log.i(LOGTAG, "current filter is " + searchFilters.toString());
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//generated required code
				
			}
			
		});
		
	}

	/**
	 * adds word to chosen vocacb list on a long click
	 */
	
	protected void addOnWordLongClick () {

		//holds the list view and sets on click listener
		ListView lv = DictionaryActivity.this.getListView();
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//allows a word to pop up
					showWordAddAlert(words.get(position));
				return false;
			}
		});
	}

	/**
	 * pops up a dialog alert, prompting the user to choose a vocab list to add
	 * to the word
	 * @param word - word to be added to the vocab list
	 * @return  Just returns true to show that the function went through
	 */
	private boolean showWordAddAlert(final Word word) {

		//sets up dialog with a title, list and a cancel button.
		AlertDialog.Builder alert = new AlertDialog.Builder(DictionaryActivity.this);
		alert.setTitle("Which list do you want to add " + word.getEnglish() + " to?");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(DictionaryActivity.this, 
				android.R.layout.select_dialog_singlechoice);
		adapter.addAll(datasource.findAllList());
		alert.setCancelable(false);

		//sets the button to create a new list, in case the User wnats to add the word
		//to a new list
		alert.setPositiveButton("Create New List", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

				//addWordToList actually adds the word to a *NEW* list
				addWordToList(word);
				dialog.dismiss();
			}
		});

		//sets the list adapter and allows the user to click on already created
		//lists if the user wants to add to the already created list.
		alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(LOGTAG, adapter.getItem(which) + "added in to yar" );
				selectedFile = adapter.getItem(which);
				//this adds the word to an already generated list
				datasource.addWord(DictionaryActivity.this, selectedFile, word);
				dialog.dismiss();
			}
		});

		//shows alert
		alert.show();
	
		return true;
	}

	/**This shows a dialog interface, allows the user to create a new list in the database, checks the list name
	 * to make sure that the there are no duplicate names, and then adds the word to the new list.
	 *
	 * @param word - This is the word which will be added to the new list
	 */
	protected void addWordToList(final Word word) {

		//sets the dialog up with a title, an edit text for the dialogue,
		AlertDialog.Builder createList = new AlertDialog.Builder(DictionaryActivity.this);
		createList.setTitle("Name your new list.");
		final EditText et = new EditText(DictionaryActivity.this);
		createList.setCancelable(false);
		createList.setView(et);
		createList.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//if the list name has already been used, the user is prompted to usea  different name
				if(datasource.findAllList().contains(et.getText().toString())){
					Toast toast = Toast.makeText(DictionaryActivity.this, 
							"You already have used this list name! Try again with a unique name.", 
							Toast.LENGTH_LONG);
					toast.show();
				}
				//makes sure something is written in the name.
				else if (et.getText().toString().length() != 0) {
					Log.i(LOGTAG, "String length: " + et.getText().toString().length());
					datasource.addVocabList(DictionaryActivity.this, et.getText().toString());
					datasource.addWord(DictionaryActivity.this, et.getText().toString(), word);
				}
				//if anything else goes wrong, it asks the user to enter a name
				else {
					Toast warning = Toast.makeText(DictionaryActivity.this,
							"Please enter a name for the list.", Toast.LENGTH_LONG);
					warning.show();
				}
			}
		});

		createList.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			//dismisses the dialogue on the cancel button
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		createList.show();
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 * basic onListItemCLick
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//gets the certain word which was clicked on
		Word word = words.get(position);
		//makes sure to show that the vocab definitely isn't open
		isVocabOpen = false;
		//Bundles information and starts intent to go to word detail activity
		Intent intent = new Intent(DictionaryActivity.this, WordDetailActivity.class);
		intent.putExtra(".model.Word", word);
		intent.putExtra("isVocabOpen", isVocabOpen);
		//goes to word detail activity
		startActivityForResult(intent, WORD_DETAIL_ACTIVITY);
	}

	/**
	 * Makes the menu always shown, so that some users aren't confused by the lack of a menu bar
	 */
    private void makeActionOverflowMenuShown() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.d(LOGTAG, e.getLocalizedMessage());
        }
    }
	
	//makes sure datasource is open on resume
	protected void onResume() {
		super.onResume();
		datasource.open();
	}

	//shuts down datasource on pause
	@Override
	protected void onPause() {
		super.onPause();
		datasource.close();
	}
		
}
