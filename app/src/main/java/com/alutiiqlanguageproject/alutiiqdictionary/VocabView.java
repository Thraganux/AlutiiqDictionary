package com.alutiiqlanguageproject.alutiiqdictionary;

import java.lang.reflect.Field;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alutiiqlanguageproject.alutiiqdictionary.db.WordsDataSource;
import com.alutiiqlanguageproject.alutiiqdictionary.model.Word;

/**
 * shows the list of words in a VocabList, and if a word or list needs to be deleted
 * this is the class/activity in which those are deleted
 */
public class VocabView extends ListActivity {

	WordsDataSource datasource; //database
	List<Word> words; //holds the list of words in the vocablist
	public TextView listOpen;
	public boolean isVocabOpen; //is sent to wordDetailActivity to signal that it should use the vocab view
	private boolean deleteWord = false; // records whether a certain word should be deleted (from worddetailview)
	private Word wordDelete; //word to delete, if exists
	private boolean listDelete = false; //records whether a list should be deleted
	private String listName; //the name of the list being shown

	//codes for sending parcels and bundles between activities.
	private static final int DICTIONARY_ACTIVITY = 1001;
	private static final int VOCAB_LISTS_ACTIVITY = 1011;
	private static final int VOCAB_PRACTICE_ACTIVITY =1101;
	private static final int WORD_DETAIL_ACTIVITY = 1111;
	
	private static final String LOGTAG = "alutiiqDict";
	
	//TODO implement a transfer to vocabpracticeactivity  which will allow the user to go from this list
		// to the practicevocab view
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocab_view);

		//takes all the information from the last activity, either
		//word detail activity or vocab chooser activity
		//and tells whether a word needs to be deleted
		Bundle b = getIntent().getExtras();
		isVocabOpen = b.getBoolean("isVocabOpen");
		listName = b.getString("listName");
		deleteWord = b.getBoolean("deleteWord");
		wordDelete = b.getParcelable(".model.Word");
		
		//opens database
		datasource = new WordsDataSource(this);
		datasource.open();

		//checks whether a word needs to be deleted before the wordList is
		//set in the list view
		if(deleteWord) {
			datasource.removeWord(this, listName, wordDelete);
		}

		//finds name and sets name of current list in the view
		listOpen = (TextView) findViewById(R.id.listName);
		listOpen.setText(listName);

		//gets all of the words, or says there are no words
		words = datasource.getVocabWords(listName);
		if (words.size() == 0) {
			Log.i(LOGTAG, "words size " +  words.size());
			Toast noWords = Toast.makeText(this, "There are no words In this List!", Toast.LENGTH_LONG);
			noWords.show();
		}


        makeActionOverflowMenuShown();//makes menu shown
		addListItemLongClickable(); //add long click to words
		refreshDisplay();//refreshes/sets display.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vocab_view, menu);
		menu.findItem(R.id.deleteList).setVisible(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//allows the user to delete the list, shows a dialogue
		if (id == R.id.deleteList) {
		
			showDeleteListAlert();
		}
		
		//sets user to main screen
		if (id == R.id.toMain){
			Intent intent = new Intent(VocabView.this, MainActivity.class);
			isVocabOpen = false;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
		}

		//sends user to dictionary
		if(id == R.id.toDictionary){
			Intent intent = new Intent(VocabView.this, DictionaryActivity.class);
			isVocabOpen = true;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
	}

		//sends user to vocab list
		if ( id == R.id.toVocabList){
			Intent intent = new Intent(VocabView.this, VocabListActivity.class);
			isVocabOpen = true;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
		}

		//sends user to settings
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	/**
	 * sets or updates the display, usually after a word has been deleted
	 */
	public void refreshDisplay(){
		Log.i(LOGTAG, "refreshed display in vocabview");
		
		ArrayAdapter<Word> adapter = new WordListAdapter(VocabView.this, words);
		setListAdapter(adapter);
	}

	/** allows the user to see the word detail after long click on the word
	 * sends the user to the wordDetailActivity
	 *
	 * @param l - the parent listview which is being clicked on
	 * @param v - the unique view that the user is clicking on
	 * @param position - number in the list
	 * @param id
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Word word = words.get(position); //gets the distinct word
		isVocabOpen = true; //tells WordDEtailActivity that the vocab is open
		Intent intent = new Intent(VocabView.this, WordDetailActivity.class);
		intent.putExtra(".model.Word", word);
		intent.putExtra("isVocabOpen", isVocabOpen);
		intent.putExtra("listName", listName);
		
		startActivityForResult(intent, WORD_DETAIL_ACTIVITY);
	}

	/**
	 * if the user wants to delete a word, makes a dialogue pop up confirming
     * that the user indeed wants to delete the list
	 */
	protected void showDeleteListAlert() {

        //sets up viw of alertDialogue with a title, and positive button
		AlertDialog.Builder alert = new AlertDialog.Builder(VocabView.this);
		alert.setTitle("Do you want to delete the whole list?");
		alert.setCancelable(false); //for kindle devices, sometimes these alert dialogues are randomly
        //cancelable by anything.  I have not actually figured out why kindle devices will cancel
        //alert dialogues.
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
                //sets a boolean to inform the vocabListActivity that this list will be deleted upon
                //arrival in the vocabListActivity
				listDelete = true;
				Intent intent = new Intent(VocabView.this, VocabListActivity.class);
				intent.putExtra("isVocabOpen", isVocabOpen);
				intent.putExtra("listName", listName);
				intent.putExtra("listDelete", listDelete);
				//sends user back to VocabListActivity
				startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);
			}
		});
		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.dismiss();
			}
		});
		alert.show();
	}

    /**
     * shows delete alert option on long click when user
     * wants to delete a word from the list
     */
	public void addListItemLongClickable() {
		ListView lv = VocabView.this.getListView();
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showWordDeleteAlert(words.get(position));
				return false;
			}
			
		});
	}

    /**
     * shows an alert that asks the user whether the user wants to delete a certain word.
     * @param word
     */
	public void showWordDeleteAlert(final Word word) {

        //sets up alert dialogue view with a title and buttons
		AlertDialog.Builder alert = new AlertDialog.Builder(VocabView.this);
		alert.setTitle("Do you want to delete " + word.getEnglish() + " from your list?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
                //deletes word
				datasource.removeWord(VocabView.this, listName, word);
				words = datasource.getVocabWords(listName);
				refreshDisplay();
				
			}
		});
		
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			//doesn't delete word
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
		
	}

    /**
     * forces the action overflow menu to be shown on devices that have a hardware button
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
	
}
