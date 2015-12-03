package com.alutiiqlanguageproject.alutiiqdictionary;

/**
 * This type of main screen is frankly unimportant and not usually desirable, but
 * this app is also targeted towards an audience that may not intuitively understand
 * current app-UI design standards , therefore this was added to give the audience a better 
 * idea of how to navigate the app.
 * 
 */

import com.alutiiqlanguageproject.alutiiqdictionary.db.WordsDataSource;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
	
	//button resource
	Button toDictionary;
	Button toVocabList;
	Button toVocabPractice;
    Button toSettings;
	
	private boolean isVocabOpen; //checks whether the vocab is open for multi-use activities such as DetailView

	//Datasource for the database
	WordsDataSource datasource;
	private String selectedFile;
	
	//Request codes which are sent between activities
	//when bundling objects to send to another activity
	private static final int DICTIONARY_ACTIVITY = 1001;
	private static final int VOCAB_LISTS_ACTIVITY = 1011;
	private static final int VOCAB_PRACTICE_ACTIVITY =1101;
	private static final int WORD_DETAIL_ACTIVITY = 1111;
	private static final String LOGTAG = "alutiiqDict";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//opens database
		datasource = new WordsDataSource(this);
		datasource.open();
		
		
		//Sets up button views
		toDictionary = (Button) findViewById(R.id.toDictionary);
		toVocabList = (Button) findViewById(R.id.toVocabList);
		toVocabPractice = (Button) findViewById(R.id.toVocabPractice);
        toSettings = (Button) findViewById(R.id.settings);
		
		//adds the logic to click on the views
		addOnClickListeners();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * This sets all of the onClickListeners
	 * Possibly just needs to be broken into distinct functions.
	 */
	protected void addOnClickListeners() {
		
		/*sets the dictionary button click sensing.  Goes to Dictionary Activity onclick*/
		toDictionary.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, DictionaryActivity.class);
				intent.putExtra("isVocabOpen", isVocabOpen);
				
				startActivityForResult(intent, DICTIONARY_ACTIVITY);	
			}
		});
		
		/* sets the vocabList button click sensing.  Starts Vocab List Activity on click*/
		toVocabList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, VocabListActivity.class);
				isVocabOpen = true;
				intent.putExtra("isVocabOpen", isVocabOpen);
				
				startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
			}
		});

		/* sets the practice vocab button click sensing. starts the vocab practice  activity on clic*/
		toVocabPractice.setOnClickListener(new OnClickListener() {
	
			

			@Override
			public void onClick(View v) {
				//opens an alert which allows the user to choose which
				//vocab list the user wishes to practice
				showAlertList();
				
				
				
				
			}
		});

		//sets the settings button on click sensing.  Starts the settings activity on click
        toSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });
	}

	/**
	 *
	 */
	
	public void showAlertList() {
		Log.i(LOGTAG, "emter list altert main");

		//sets up builder for alert dialog as well as UI of dialogue
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setTitle("Choose a practice list.");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, 
				android.R.layout.select_dialog_singlechoice);
		Log.i(LOGTAG, "database is adding list");
		alert.setCancelable(false);


		boolean isnull = datasource.findAllList().isEmpty();
		Log.i(LOGTAG, "database is null? "+ isnull);
		adapter.addAll(datasource.findAllList());
		Log.i(LOGTAG, "setting up stuff0");

		//allows user to dismiss the alert dialogue
		alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});

		//sets a clickable list that allows the user to choose which list to
		alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				selectedFile = adapter.getItem(which);
				Intent intent = new Intent(MainActivity.this, VocabPracticeActivity.class);
				isVocabOpen = true;
				intent.putExtra("isVocabOpen", isVocabOpen);
				intent.putExtra("practiceList", selectedFile);
				
				startActivityForResult(intent, VOCAB_PRACTICE_ACTIVITY);
				
				dialog.dismiss();
			}
		});
		alert.show();
	}
	
}



