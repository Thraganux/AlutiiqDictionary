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
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alutiiqlanguageproject.alutiiqdictionary.db.WordsDataSource;
import com.alutiiqlanguageproject.alutiiqdictionary.model.Word;

public class VocabListActivity extends ListActivity {

	//allocates graphical resources
	public Button createButton;
	public EditText createName;
	//allocates place for datasource
	WordsDataSource datasource;
	
	//informs whether the person wants to delete a list.
	//The list is deleted upon reentry to the vocab list activity
	//since the vocab view depends upon a list entry, it is 
	// not a good idea to try to delete the vocab list while in the list view
	private boolean listDelete = false;
	//name of list to be deleted
	private String listToDelete;
	//shows that the vocab list is open for when the user goes into the detail view
	private boolean isVocabOpen = true;
	//holds the list of vocab list names
	private List<String> listNames;
	
	//Request codes
	private static final int DICTIONARY_ACTIVITY = 1001;
	private static final int VOCAB_LISTS_ACTIVITY = 1011;
	private static final int VOCAB_PRACTICE_ACTIVITY =1101;
	private static final int WORD_DETAIL_ACTIVITY = 1111;

	
	//TODO implement a transfer to vocabpracticeactivity  which will allow the user to go from this list
	// to the practice
	
	private static final String LOGTAG = "alutiiqDict";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocab_list);
		
		//grabs the bundled info
		Bundle b = getIntent().getExtras();
		isVocabOpen = b.getBoolean("isVocabOpen");
		listDelete = b.getBoolean("listDelete");
		listToDelete = b.getString("listName");
		
		//sets up and opens datasource
		datasource = new WordsDataSource(this);
		datasource.open();
		
		//checks if there is a list to be deleted *BEFORE* populating the
		//list view with the names of all the vocab lists
		if(listDelete) {
			datasource.removeVocabList(this, listToDelete);
		}
		
		//populates the list with all the names of the vocab lists
		listNames = datasource.findAllList();
		Log.i(LOGTAG, "listnames contains "  + listNames.size());
		
		//warns the user that s/he doesn't have any vocab lists set up
		if(listNames.size() == 0){
			Toast alert = Toast.makeText(this, "You do not have any vocab lists! Create one and add words.", 
					Toast.LENGTH_LONG);
			alert.show();
		}

        makeActionOverflowMenuShown();
		//sets up all the graphical click-items
		addCreateListDeleteOnClick();
		addCreateOnButtonClick();
		refreshDisplay();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vocab_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		//Sends user back to main
		if (id == R.id.toMain){
			Intent intent = new Intent(VocabListActivity.this, MainActivity.class);
			isVocabOpen = false;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
	}
		//sends user to dictionary
		if(id == R.id.toDictionary){
			Intent intent = new Intent(VocabListActivity.this, DictionaryActivity.class);
			isVocabOpen = true;
			intent.putExtra("isVocabOpen", isVocabOpen);
			
			startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
	}
		
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * refreshes display upon names taken away or added
	 */
	public void refreshDisplay() {
		Log.i(LOGTAG, "refreshed display in vocablistview");
		ArrayAdapter<String> adapter = new ListNameArrayAdapter(VocabListActivity.this, listNames);
		this.setListAdapter(adapter);
	}
	
	/*
	 * sets up button clicks
	 */
	public void addCreateOnButtonClick() {
		
		//finds views
		createButton = (Button) findViewById(R.id.createList);
		createName = (EditText) findViewById(R.id.editName);
		createButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//gets the texted entered in the editview
				String newName = createName.getText().toString();
				if(datasource.findAllList().contains(newName)){
					Toast toast = Toast.makeText(VocabListActivity.this, 
							"You already have a list by this name! Try again with a unique name.", 
							Toast.LENGTH_LONG);
					toast.show();
				}
				else if (createName.getText().toString().length() == 0) {
					Toast toast = Toast.makeText(VocabListActivity.this, 
							"Please enter a name for your list.", Toast.LENGTH_LONG);
					toast.show();
				}
				//adds new name to vocablist database
				else {
				datasource.addVocabList(VocabListActivity.this, newName);
				Log.i(LOGTAG, newName + "added");
				//refreshes display for the new vocab list name added
				listNames = datasource.findAllList();
				}
				refreshDisplay();
				
			}
			
		});
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 * checks which item was clicked
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//gets name of item clicked
		String name = listNames.get(position);
		isVocabOpen = true;
		//bundles info
		Intent intent = new Intent(VocabListActivity.this, VocabView.class);
		intent.putExtra("listName", name);
		intent.putExtra("isVocabOpen", isVocabOpen);
		
		startActivityForResult(intent, WORD_DETAIL_ACTIVITY);
	}
	
	/*
	 * shows a delete dialogue when user longclicks an item
	 */
	protected void addCreateListDeleteOnClick() {
		ListView lv = VocabListActivity.this.getListView();
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//gives user option to delete list
				showListDeleteAlert(listNames.get(position));
				return false;
			}
		});
	}
	
	/*
	 * shows a delete alert and gives the user the option to delete a list.
	 */
	protected void showListDeleteAlert(final String listName) {
		AlertDialog.Builder alert = new AlertDialog.Builder(VocabListActivity.this);
		alert.setCancelable(false);
		alert.setTitle("Do you want to delete list: " + listName);
		
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//deletes list
				datasource.removeVocabList(VocabListActivity.this, listName);
				//recreates names of lists
				listNames = datasource.findAllList();
				//shows list of names without the deleted list's name
				refreshDisplay();
				
			}
		});
		//gets rid of the alert dialogue if the user does not want to delete the list
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
