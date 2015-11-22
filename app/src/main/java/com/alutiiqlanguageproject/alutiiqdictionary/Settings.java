package com.alutiiqlanguageproject.alutiiqdictionary;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;


public class Settings extends ActionBarActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RadioGroup dialect;
    Spinner spinner;

    ArrayList<String> fontSizes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fontSizes = new ArrayList<String>();

        fontSizes.add("12");
        fontSizes.add("16");
        fontSizes.add("20");
        fontSizes.add("24");

        pref = getApplicationContext().getSharedPreferences("DictPref", MODE_PRIVATE);
        editor = pref.edit();

        addSpinner();
        addRadioGroup();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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




    private void addSpinner() {
        Spinner spinner = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, fontSizes );
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    editor.putString("fontSize", fontSizes.get(position));
                                  editor.commit();
              }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {

         }
     }   );
                }

    private void addRadioGroup() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup2);
        final RadioButton aluNorth = (RadioButton) findViewById(R.id.chooseNorth);
        final RadioButton aluSouth = (RadioButton) findViewById(R.id.chooseSouth);
        final RadioButton both = (RadioButton) findViewById(R.id.chooseBoth);

        String curCheck = "";

        if (pref.getString("dialectPref", null) != null){
            curCheck = pref.getString("dialectPref", null);
        }
        if (curCheck != null && curCheck.equals("aluNorth")) {
            aluNorth.setChecked(true);
        }
        else if (curCheck != null && curCheck.equals("aluSouth")) {
            aluSouth.setChecked(true);
        }
        else if (curCheck != null && curCheck.equals("both")){
            both.setChecked(true);
        }
        else {

        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.chooseNorth) {
                    aluNorth.setChecked(true);
                    editor.putString("dialectPref", "aluNorth");
                    editor.commit();
                }
                else if (checkedId == R.id.chooseSouth) {
                    aluSouth.setChecked(true);
                    editor.putString("dialectPref", "aluSouth");
                    editor.commit();
                }
                else if (checkedId == R.id.chooseBoth) {
                    both.setChecked(true);
                    editor.putString("dialectPref", "both");
                    editor.commit();
                }
                else {

                }
            }
        });
    }

}
