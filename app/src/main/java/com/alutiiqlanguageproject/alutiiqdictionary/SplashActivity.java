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

    WordsDataSource datasource;
    List<Word> words;

    TextView tx;

    private static final String LOGTAG = "alutiiqDictionary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        datasource = new WordsDataSource(SplashActivity.this);
        datasource.open();
        tx = (TextView) findViewById(R.id.loadingText);

        words = datasource.findAll();
        if (words.size() == 0) {
        new AsyncLoadXMLFeed().execute();

        }
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
            // show your progress dialog

        }

        @Override
        protected Void doInBackground(Void... voids){
            // load your xml feed asynchronously
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
            // dismiss your dialog
            // launch your News activity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // close this activity
            finish();
        }

    }

}
