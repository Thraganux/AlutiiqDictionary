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

public class VocabView extends ListActivity {

	WordsDataSource datasource;
	List<Word> words;
	public TextView listOpen;
	public boolean isVocabOpen;
	private boolean deleteWord = false;
	private Word wordDelete;
	private boolean listDelete = false;
	private String listName;
	
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
		
		Bundle b = getIntent().getExtras();
		isVocabOpen = b.getBoolean("isVocabOpen");
		listName = b.getString("listName");
		deleteWord = b.getBoolean("deleteWord");
		wordDelete = b.getParcelable(".model.Word");
		
		
		
		
		datasource = new WordsDataSource(this);
		datasource.open();
		
		if(deleteWord) {
			datasource.removeWord(this, listName, wordDelete);
		}
		
		listOpen = (TextView) findViewById(R.id.listName);
		listOpen.setText(listName);
		
		words = datasource.getVocabWords(listName);
		if (words.size() == 0) {
			Log.i(LOGTAG, "words size " +  words.size());
			Toast noWords = Toast.makeText(this, "There are no words In this List!", Toast.LENGTH_LONG);
			noWords.show();
		}

        makeActionOverflowMenuShown();
		addListItemLongClickable();
		refreshDisplay();
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
		
		if (id == R.id.deleteList) {
		
			showDeleteListAlert();
		}
		
		
		if (id == R.id.toMain){
			Intent intent = new Intent(VocabView.this, MainActivity.class);
			isVocabOpen = false;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
		}
		
		if(id == R.id.toDictionary){
			Intent intent = new Intent(VocabView.this, DictionaryActivity.class);
			isVocabOpen = true;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
	}
		
		if ( id == R.id.toVocabList){
			Intent intent = new Intent(VocabView.this, VocabListActivity.class);
			isVocabOpen = true;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
		}
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void refreshDisplay(){
		Log.i(LOGTAG, "refreshed display in vocabview");
		
		ArrayAdapter<Word> adapter = new WordListAdapter(VocabView.this, words);
		setListAdapter(adapter);
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Word word = words.get(position);
		isVocabOpen = true;
		Intent intent = new Intent(VocabView.this, WordDetailActivity.class);
		intent.putExtra(".model.Word", word);
		intent.putExtra("isVocabOpen", isVocabOpen);
		intent.putExtra("listName", listName);
		
		startActivityForResult(intent, WORD_DETAIL_ACTIVITY);
	}
	
	protected void showDeleteListAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(VocabView.this);
		alert.setTitle("Do you want to delete the whole list?");
		alert.setCancelable(false);
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listDelete = true;
				Intent intent = new Intent(VocabView.this, VocabListActivity.class);
				intent.putExtra("isVocabOpen", isVocabOpen);
				intent.putExtra("listName", listName);
				intent.putExtra("listDelete", listDelete);
				
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
	public void showWordDeleteAlert(final Word word) {
		AlertDialog.Builder alert = new AlertDialog.Builder(VocabView.this);
		alert.setTitle("Do you want to delete " + word.getEnglish() + " from your list?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				datasource.removeWord(VocabView.this, listName, word);
				words = datasource.getVocabWords(listName);
				refreshDisplay();
				
			}
		});
		
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
		
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
