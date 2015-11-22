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

	WordsDataSource datasource;
	private String selectedFile;
	
	//Request codes
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
	
	protected void addOnClickListeners() {
		
		/*sets the dictionary onClick.  Goes to Dictionary Activity*/
		toDictionary.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, DictionaryActivity.class);
				intent.putExtra("isVocabOpen", isVocabOpen);
				
				startActivityForResult(intent, DICTIONARY_ACTIVITY);	
			}
		});
		
		/* sets the vocabList onClick.  Starts Vocab List Activity */
		toVocabList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, VocabListActivity.class);
				isVocabOpen = true;
				intent.putExtra("isVocabOpen", isVocabOpen);
				
				startActivityForResult(intent, VOCAB_LISTS_ACTIVITY);	
			}
		});

		/* starts the vocab practice activity */
		toVocabPractice.setOnClickListener(new OnClickListener() {
	
			

			@Override
			public void onClick(View v) {
				showAlertList();
				
				
				
				
			}
		});

        toSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });
	}
	
	public void showAlertList() {
		Log.i(LOGTAG, "emter list altert main");
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setTitle("Choose a practice list.");
		Log.i(LOGTAG, "setting up list");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, 
				android.R.layout.select_dialog_singlechoice);
		Log.i(LOGTAG, "database is adding list");
		alert.setCancelable(false);
		boolean isnull = datasource.findAllList().isEmpty();
		Log.i(LOGTAG, "database is null? "+ isnull);
		adapter.addAll(datasource.findAllList());
		Log.i(LOGTAG, "setting up stuff0");
		alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		
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



