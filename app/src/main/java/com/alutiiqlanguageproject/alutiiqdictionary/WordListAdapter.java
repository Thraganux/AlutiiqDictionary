package com.alutiiqlanguageproject.alutiiqdictionary;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alutiiqlanguageproject.alutiiqdictionary.model.Word;

import static com.alutiiqlanguageproject.alutiiqdictionary.R.color;
import static com.alutiiqlanguageproject.alutiiqdictionary.R.color.whiteTrans;


public class WordListAdapter extends ArrayAdapter<Word> {

        //he context is required in order to set up the view
	Context context;
        //hols the list of words which will be set, on ein each individual list Item
	List<Word> words;

	//Constructor for the listeView
	public WordListAdapter(Context context, List<Word> words) {
		super(context, android.R.id.content, words);
		this.context = context;
		this.words = words;
	}

        /**
         * just another overriden method for the in the arrayadapter class.
         * sets up each individual item in the list with their view layouts information.
         * @param position - position of item
         * @param convertView
         * @param parent - parent of the list
         * @return
         */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        View view = vi.inflate(R.layout.listitem_word, null);

            //chooses the appropriate word for the order of that position. eg. "apple" is in position one, "bear" position two.
        // more or less alphabetically
        Word word = words.get(position);

        //frilly graphics to allow the user to see the difference between each clickable word on the list.
        //grey or white depending on its place
        if((position % 2) == 0) {
            view.setBackgroundColor(Color.parseColor("#4DBABABA"));
        }
        else {
            view.setBackgroundColor(Color.parseColor("#4DF2F2F2"));
        }

        //sets the english word textView
        TextView english = (TextView) view.findViewById(R.id.english);
        english.setText(word.getEnglish());

        //sets the alutiiq north text view
        TextView aluNorth = (TextView) view.findViewById(R.id.aluNorth);
        aluNorth.setText("Alutiiq North: " + word.getAluNorth());

        //sets the alutiiq south text view
        TextView aluSouth = (TextView) view.findViewById(R.id.aluSouth);
        aluSouth.setText("Alutiiq South: " + word.getAluSouth());
        
        return view;
	}
	}
