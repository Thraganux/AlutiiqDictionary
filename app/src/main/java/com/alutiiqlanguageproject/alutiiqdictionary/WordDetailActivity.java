package com.alutiiqlanguageproject.alutiiqdictionary;

import java.lang.reflect.Field;
import java.util.List;

import javax.sql.DataSource;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;












import android.widget.Toast;

import com.alutiiqlanguageproject.alutiiqdictionary.db.WordsDataSource;
import com.alutiiqlanguageproject.alutiiqdictionary.model.Word;

/**
 * this class is accessed by BOTH the dicionary activity AND the vocab activity.  It changes slightly
 * depending on where the user accessed it from.
 * in teh case that the user accessed it from the dictionary, it allows the user to add this word to a vocab list
 * in the case that it's accessed from a vocab list, the detail shows which list
 */
public class WordDetailActivity extends ActionBarActivity {

	//checks whether the vocab list is actually open
	private boolean isVocabOpen;
	//holds the word object which is being looked at
	private Word word;
	//database
	WordsDataSource datasource;
	//holds which file was selected
	private String selectedFile;
	//if the user chooses to *delete the word* from the vocab lists
	//this value will be changed to true, so that the word isn't deleted immediately from the database
	//which can cause errors.
	//the word is tehn deleted upon entering another activity.
	private boolean delete = false;
	
	private static final String LOGTAG = "alutiiqDict";

	//request codes for sending/parcelling information from one activity to another
	private static final int DICTIONARY_ACTIVITY = 1001;
	private static final int VOCAB_LISTS_ACTIVITY = 1011;
	private static final int VOCAB_PRACTICE_ACTIVITY =1101;
	private static final int WORD_DETAIL_ACTIVITY = 1111;

	/**
	 * standard on create activity/view
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_detail);

		//This Bundle b holds the information that was sent over from the last activity
		Log.i(LOGTAG, "Starting detail activity");
		Bundle b = getIntent().getExtras();
		//Checks whether vocab is open, and whether the user is accessing the word detail from the
		//vocab list that the User created, or the dictionary
		isVocabOpen = b.getBoolean("isVocabOpen");
		//This word/parcelable function gets the word object that the user chose from the last activity
		//so that the information for the word may be displayed in detail
		word = b.getParcelable(".model.Word");
		//Gets the name of the file that was selected, in the case that one is
		selectedFile = b.getString("listName");
		
		//holds datasource
		datasource = new WordsDataSource(this);
		datasource.open();

        //makes it so overflow menu is always shown
        makeActionOverflowMenuShown();
		//refreshes display in the case of a change
		refreshDisplay();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.word_detail, menu);
		
		//shows add button when vocab is not open
		Log.i(LOGTAG, "is vocab open " + isVocabOpen);
		menu.findItem(R.id.addWord).setVisible(!isVocabOpen);
		
		//shows delete button when vocab is open
		menu.findItem(R.id.deleteWord).setVisible(isVocabOpen);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//adds the word being looked at to a vocab list.
		if (id == R.id.addWord) {
			showAlertAdd();
			
			Log.i(LOGTAG, word.getEnglish() + " added");
		}
		//deletes the word in the case that it is on a vocab list
		if (id ==  R.id.deleteWord) {
			showAlertDelete();

		}
			//sends the user back to main
		if (id == R.id.toMain){
			Intent intent = new Intent(WordDetailActivity.this, MainActivity.class);
			isVocabOpen = false;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
	}
		//sends the user to the dictionary, if the vocab is open
		if(id == R.id.toDictionary){
			Intent intent = new Intent(WordDetailActivity.this, DictionaryActivity.class);
			isVocabOpen = true;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
	}
		//sends the user to the vocab lists, if the dictionary is open
		if ( id == R.id.toVocabLists){
			Intent intent = new Intent(WordDetailActivity.this, VocabListActivity.class);
			isVocabOpen = true;
			intent.putExtra("isVocabOpen", isVocabOpen);
			Log.i(LOGTAG, "vocablistsopen");
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * updates the information.
	 * Possible To do: allow the user to edit the information.
	 */
	private void refreshDisplay(){
		TextView english = (TextView) findViewById(R.id.englishWord);
		english.setText("English: " + word.getEnglish());
		TextView aluNorth = (TextView) findViewById(R.id.aluNorth);
		aluNorth.setText("Alutiiq North: " + word.getAluNorth());
		TextView aluSouth = (TextView) findViewById(R.id.aluSouth);
		aluSouth.setText("Alutiiq South: " + word.getAluSouth());
		TextView category = (TextView) findViewById(R.id.category);
		category.setText("Category: " + word.getCategory());
		TextView function = (TextView) findViewById(R.id.function);
		function.setText("Function: " + word.getFunction());
		TextView comments = (TextView) findViewById(R.id.comments);
		comments.setText(word.getComments());
	}
	//standard on resume, opents datasource
	protected void onResume() {
		super.onResume();
		datasource.open();
	}

	//standard on pause, aso closes datasource
	@Override
	protected void onPause() {
		super.onPause();
		datasource.close();
	}

	/**
	 * allows the user to choose a vocab list for the word to be added to
	 * @return
	 */
	public boolean showAlertAdd() {

		//Sets up the title, cancel button and list view of the dialogue
		AlertDialog.Builder alert = new AlertDialog.Builder(WordDetailActivity.this);
		alert.setCancelable(false);
		alert.setTitle("Add To Which List");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(WordDetailActivity.this, 
				android.R.layout.select_dialog_singlechoice);
		//adds all the words tot he dialogue.
		adapter.addAll(datasource.findAllList());
		
		alert.setPositiveButton("Create New List", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//on "Create new list" sensed, this adds the word to the chosen list
				addToList();
				dialog.dismiss();
				
			}
		
		});
		
		alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//if an already created list is clicked, this chooses the aready created list
				Log.i(LOGTAG, adapter.getItem(which) + "added in to yar" );
				selectedFile = adapter.getItem(which);
				datasource.addWord(WordDetailActivity.this, selectedFile, word);
				dialog.dismiss();
			}
		});
		alert.show();
	
		return true;
	}

	/**
	 * this shows a new dialogue which prompts the user to create a new name for a new list
	 * and then adds the word to the list
	 */
	protected void addToList() {
		//sets up title, buttons and edit text
		AlertDialog.Builder createList = new AlertDialog.Builder(WordDetailActivity.this);
		createList.setCancelable(false);
		createList.setTitle("Name your new list.");
		final EditText et = new EditText(WordDetailActivity.this);
		createList.setView(et);

		//This button creates the list
		createList.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//checks whether the user has a duplicate name in the database
				if(datasource.findAllList().contains(et.getText().toString())){
					Toast toast = Toast.makeText(WordDetailActivity.this, 
							"You already have used this list name! Try again with a unique name.", 
							Toast.LENGTH_LONG);
					toast.show();
				}
				//makes sure the user has written something and adds the word to the new list
				else if (et.getText().toString().length() != 0) {
					datasource.addVocabList(WordDetailActivity.this, et.getText().toString());
					datasource.addWord(WordDetailActivity.this, et.getText().toString(), word);
				}
				//warns the user to enter a new name
				else {
					Toast warning = Toast.makeText(WordDetailActivity.this,
							"Please enter a name for the list.", Toast.LENGTH_LONG);
					warning.show();
				}
			}
		});
		
		createList.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		createList.show();
	}

	/**
	 * shows a dialogue, asking whether the user wants to delete a word from the vocab list.
	 * if so, it sets the delete boolean to true, so that in the next activity, word will be deleted later
	 * @return
	 */
	public boolean showAlertDelete() {
		//sets up dialogue
		AlertDialog.Builder alert = new AlertDialog.Builder(WordDetailActivity.this);
		alert.setTitle("Delete entry?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//sets delete to true
				delete = true;
				Intent intent = new Intent(WordDetailActivity.this, VocabView.class);
				isVocabOpen = true;
				intent.putExtra("isVocabOpen", isVocabOpen);
				intent.putExtra(".model.Word", word);
				intent.putExtra("deleteWord", delete);
				intent.putExtra("listName", selectedFile);

				//sends the user back to the vocab list activity, whereupon the word will be deleted from the selected list
				startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);
				
			}
		});

		//allows the user to cancel the delete request
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
		return true;
	}
	
	public boolean alertOptionNewList() {
		//TODO implement alert upon either choosing to create a new list
		return true;
	}

	/**
	 * requires the overflow menu to be shown no matter what.
	 */
    private void makeActionOverflowMenuShown() {
        //devices with hardware menu button don't show action overflow menu
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
