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
	
	Context context;
	List<Word> words;

	
	public WordListAdapter(Context context, List<Word> words) {
		super(context, android.R.id.content, words);
		this.context = context;
		this.words = words;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        View view = vi.inflate(R.layout.listitem_word, null);


        Word word = words.get(position);

        if((position % 2) == 0) {
            view.setBackgroundColor(Color.parseColor("#4DBABABA"));
        }
        else {
            view.setBackgroundColor(Color.parseColor("#4DF2F2F2"));
        }
        
        TextView english = (TextView) view.findViewById(R.id.english);
        english.setText(word.getEnglish());
        
        TextView aluNorth = (TextView) view.findViewById(R.id.aluNorth);
        aluNorth.setText("Alutiiq North: " + word.getAluNorth());
        
        TextView aluSouth = (TextView) view.findViewById(R.id.aluSouth);
        aluSouth.setText("Alutiiq South: " + word.getAluSouth());
        
        return view;
	}
	}
