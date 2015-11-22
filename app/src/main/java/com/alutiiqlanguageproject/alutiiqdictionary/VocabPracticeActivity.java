package com.alutiiqlanguageproject.alutiiqdictionary;



import java.lang.reflect.Field;
import java.util.List;

import com.alutiiqlanguageproject.alutiiqdictionary.db.WordsDataSource;
import com.alutiiqlanguageproject.alutiiqdictionary.model.Word;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VocabPracticeActivity extends ActionBarActivity {
	
	private boolean mShowingBack = false;
	public List<Word> words;
	private String listName;
	Button forward;
	Button back;

    SharedPreferences pref;
	
	protected static final String LOGTAG = "alutiiqFrag";
	WordsDataSource datasource;
	private int placeInList = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocab_practice);
		
		Bundle b = getIntent().getExtras();
		listName = b.getString("practiceList");
		Log.i(LOGTAG, "Got list name " + listName);
		
		//opens the database to get the vocab lists
		datasource = new WordsDataSource(this);
		datasource.open();

        //get which dialect the user wants to practice
        pref = getApplicationContext().getSharedPreferences("DictPref", MODE_PRIVATE);
		//gets the chosen vocab list;
		words = datasource.getVocabWords(listName);
		
		//sets buttons up 
		forward = (Button) findViewById(R.id.forward);
		back = (Button) findViewById(R.id.back);

        //makes the menu overflow buttons always appear on every device
        makeActionOverflowMenuShown();
		//sets the Listeners for the foward and back buttons
		addButtonListeners();
		
		//initializes the first vocab card
		if (savedInstanceState == null) {
			getFragmentManager()
			.beginTransaction()
			.add(R.id.container, new CardFrontAlutiiq())
			.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vocab_practice, menu);
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
	
	public void addButtonListeners() {
		//foward listener
		forward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Checks if the user is at the end of this list and if not
				// increments the place in list by one
				if (placeInList < words.size()) {
					placeInList = placeInList + 1;
					mShowingBack = false;
					newFragment();
				}
				else {
					//TODO make something informing the user that s/he has come to the end of the list
				}
			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//checks if the user is at the start of the list and if not, 
				//decrements the place in list
				if (placeInList != 0 ) {
				placeInList = placeInList - 1;
				mShowingBack = false;
				newFragment();
				}
				//if at start, warns user 
				else {
					Toast toast = Toast.makeText(VocabPracticeActivity.this, 
							"You are at the start of the list!", Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});
	}
		/**
		 * function to remove the old displayed fragment
		 * and creates a new one, starting with the alutiiq word
		 * 
		 */
	//TODO perhaps make the newFragment function take a currentword var, instead of initializing the word in subclass
		private void newFragment() {
			FragmentManager myFragMan = getFragmentManager();
			
			myFragMan.beginTransaction()
			.remove(getFragmentManager()
			.findFragmentById(R.id.container))
			.commit();
			
			myFragMan.beginTransaction()
			.add(R.id.container, new CardFrontAlutiiq())
			.commit();
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
	
	//fragment class for the alutiiq words

	@SuppressLint("ValidFragment")
    public class CardFrontAlutiiq extends Fragment implements android.view.View.OnClickListener {
		private Word  word = words.get(placeInList);


		public CardFrontAlutiiq() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			//sets up the fragment and text views



			View rootView = inflater.inflate(R.layout.alutiiq_front_card, container,
					false);
            //chooses dialects
            String dialect = pref.getString("dialectPref", null);
            if (dialect.equals("aluNorth")) {
                TextView northTv = (TextView) rootView.findViewById(R.id.northAlutiiq);
                northTv.setText("Alutiiq North: \n" + word.getAluNorth());
            }
            else if (dialect.equals("aluSouth")) {
                TextView southTv = (TextView) rootView.findViewById(R.id.southAlutiiq);
                southTv.setText("Alutiiq South: \n" + word.getAluSouth());
            }
            else {
                TextView northTv = (TextView) rootView.findViewById(R.id.northAlutiiq);
                northTv.setText("Alutiiq North: \n" + word.getAluNorth());
                TextView southTv = (TextView) rootView.findViewById(R.id.southAlutiiq);
                southTv.setText("Alutiiq South: \n" + word.getAluSouth());
            }


			
			FrameLayout frame = (FrameLayout) findViewById(R.id.container);
			
			//sets text for the fragment view


			
			Log.i(LOGTAG, "cardclicklistener set");
			//listens for a click to flip card
			frame.setOnClickListener(this);
			
			return rootView;
		}

        private void setAluNorth(View rootView) {

        }

        private void setAluSouth(View rootView){

        }

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i(LOGTAG, "clicked");
			
			//flips card
			flipCardToBack();
			
		}
		
		private void flipCardToBack() {
		    if (mShowingBack) {
		        getFragmentManager().popBackStack();
		        mShowingBack = false;
		        return;
		    }

		    // Flip to the back.

		    mShowingBack = true;

		    // Create and commit a new fragment transaction that adds the fragment for the back of
		    // the card, uses custom animations, and is part of the fragment manager's back stack.

		    getFragmentManager()
		            .beginTransaction()

		            // Replace the default fragment animations with animator resources representing
		            // rotations when switching to the back of the card, as well as animator
		            // resources representing rotations when flipping back to the front (e.g. when
		            // the system Back button is pressed).
		            .setCustomAnimations(
		                    R.animator.card_flip_in_right, R.animator.card_flip_out_right,
		                    R.animator.card_flip_in_left, R.animator.card_flip_out_left)

		            // Replace any fragments currently in the container view with a fragment
		            // representing the next page (indicated by the just-incremented currentPage
		            // variable).
		            .replace(R.id.container, new CardBackEnglish())

		            // Add this transaction to the back stack, allowing users to press Back
		            // to get to the front of the card.
		            .addToBackStack(null)

		            // Commit the transaction.
		            .commit();
		}
		
	}
	
	@SuppressLint("ValidFragment")
    public class CardBackEnglish extends Fragment {
		
		private Word  word = words.get(placeInList);
		
		public CardBackEnglish() {
			
		}
		
		/**
		 * sets up view for the back of the card with the english definition
		 */
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.english_back_card, container,
					false);
			
			TextView english = (TextView) rootView.findViewById(R.id.english);
			english.setText("English: \n" + word.getEnglish());
			Log.i(LOGTAG, "new english back created");
			
			return rootView;
		}
		
		
	}
}
