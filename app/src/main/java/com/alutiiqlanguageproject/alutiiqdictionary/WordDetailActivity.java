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

public class WordDetailActivity extends ActionBarActivity {
	
	private boolean isVocabOpen;
	private Word word;
	WordsDataSource datasource;
	private String selectedFile;
	private boolean delete = false;
	
	private static final String LOGTAG = "alutiiqDict";
	
	private static final int DICTIONARY_ACTIVITY = 1001;
	private static final int VOCAB_LISTS_ACTIVITY = 1011;
	private static final int VOCAB_PRACTICE_ACTIVITY =1101;
	private static final int WORD_DETAIL_ACTIVITY = 1111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_detail);
		
		Log.i(LOGTAG, "Starting detail activity");
		Bundle b = getIntent().getExtras();
		isVocabOpen = b.getBoolean("isVocabOpen");
		word = b.getParcelable(".model.Word");
		selectedFile = b.getString("listName");
		
		
		datasource = new WordsDataSource(this);

        //makes it so overflow menu is always shown
        makeActionOverflowMenuShown();
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
		if (id == R.id.addWord) {
			showAlertAdd();
			
			Log.i(LOGTAG, word.getEnglish() + " added");
		}
		if (id ==  R.id.deleteWord) {
			showAlertDelete();

		}
			
		if (id == R.id.toMain){
			Intent intent = new Intent(WordDetailActivity.this, MainActivity.class);
			isVocabOpen = false;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
	}
		
		if(id == R.id.toDictionary){
			Intent intent = new Intent(WordDetailActivity.this, DictionaryActivity.class);
			isVocabOpen = true;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
	}
		
		if ( id == R.id.toVocabLists){
			Intent intent = new Intent(WordDetailActivity.this, VocabListActivity.class);
			isVocabOpen = true;
			intent.putExtra("isVocabOpen", isVocabOpen);
			Log.i(LOGTAG, "vocablistsopen");
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
		}
		return super.onOptionsItemSelected(item);
	}
	
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
	protected void onResume() {
		super.onResume();
		datasource.open();
	}

	@Override
	protected void onPause() {
		super.onPause();
		datasource.close();
	}
	
	public boolean showAlertAdd() {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(WordDetailActivity.this);
		alert.setCancelable(false);
		alert.setTitle("Add To Which List");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(WordDetailActivity.this, 
				android.R.layout.select_dialog_singlechoice);
		adapter.addAll(datasource.findAllList());
		
		alert.setPositiveButton("Create New List", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				addToList();
				dialog.dismiss();
				
			}
		
		});
		
		alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(LOGTAG, adapter.getItem(which) + "added in to yar" );
				selectedFile = adapter.getItem(which);
				datasource.addWord(WordDetailActivity.this, selectedFile, word);
				dialog.dismiss();
			}
		});
		alert.show();
	
		return true;
	}
	
	protected void addToList() {
		AlertDialog.Builder createList = new AlertDialog.Builder(WordDetailActivity.this);
		createList.setCancelable(false);
		createList.setTitle("Name your new list.");
		final EditText et = new EditText(WordDetailActivity.this);
		createList.setView(et);
		
		createList.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(datasource.findAllList().contains(et.getText().toString())){
					Toast toast = Toast.makeText(WordDetailActivity.this, 
							"You already have used this list name! Try again with a unique name.", 
							Toast.LENGTH_LONG);
					toast.show();
				}
				else if (et.getText().toString().length() != 0) {
					datasource.addVocabList(WordDetailActivity.this, et.getText().toString());
					datasource.addWord(WordDetailActivity.this, et.getText().toString(), word);
				}
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
	
	
	public boolean showAlertDelete() {
		AlertDialog.Builder alert = new AlertDialog.Builder(WordDetailActivity.this);
		alert.setTitle("Delete entry?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				delete = true;
				Intent intent = new Intent(WordDetailActivity.this, VocabView.class);
				isVocabOpen = true;
				intent.putExtra("isVocabOpen", isVocabOpen);
				intent.putExtra(".model.Word", word);
				intent.putExtra("deleteWord", delete);
				intent.putExtra("listName", selectedFile);
				
				startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);
				
			}
		});
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

    private void makeActionOverflowMenuShown() {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
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
