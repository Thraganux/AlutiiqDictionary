package com.alutiiqlanguageproject.alutiiqdictionary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alutiiqlanguageproject.alutiiqdictionary.db.WordsDataSource;
import com.alutiiqlanguageproject.alutiiqdictionary.model.Word;
import com.alutiiqlanguageproject.alutiiqdictionary.xml.WordsPullParser;

import java.util.List;


public class SplashActivity extends ActionBarActivity {

    // Vars to check if database is populated and whether it needs to be populated
    WordsDataSource datasource;
    List<Word> words;

    //Welcome text, changes depending on whether database has already
    //been populated or not
    TextView tx;

    // for Log/debug reporting
    private static final String LOGTAG = "alutiiqDictionary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Opens the new datasource in the splash activity context
        datasource = new WordsDataSource(SplashActivity.this);
        datasource.open();

        //loads changeable text view
        tx = (TextView) findViewById(R.id.loadingText);

        //Code for checking to see if the datasource
        //has been populated yet.  If not

        words = datasource.findAll();

        // if database is empty, this clause starts an async thread to load
        //entries in to database
        if (words.size() == 0) {
            //async thread to load xml in database
        new AsyncLoadXMLFeed().execute();

        }
        //just opens the splash screen to show logo
        //and possibly advertisements
        else {

            tx.setText("Welcome!");
            Thread background = new Thread() {
                public void run() {

                    try {
                        // Thread will sleep for 5 seconds
                        sleep(5*1000);

                        // After 5 seconds redirect to another intent
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);

                        //Remove activity
                        finish();

                    } catch (Exception e) {
                        Log.i(LOGTAG, "Exception!");
                    }
                }
            };

            // start thread
            background.start();

        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Parses XML in order to populate database
     */
    private void createData() {
        Log.i(LOGTAG, "creating data");
        WordsPullParser parser = new WordsPullParser();
        List<Word> words = parser.parseXML(this);
        for (Word word : words) {
            datasource.create(word);
        }
    }

    private class AsyncLoadXMLFeed extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute(){
            // TODO create a progress dialog with a loading bar

        }

        @Override
        protected Void doInBackground(Void... voids){
            // load xml feed asynchronously
            words = datasource.findAll();
            if (words.size() == 0) {
                tx.setText(R.string.welcomeMessage);
                createData();
                words = datasource.findAll();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void params){
            // dismiss dialog
            //launch activity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // close this activity
            finish();
        }

    }

}
