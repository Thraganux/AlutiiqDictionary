package com.alutiiqlanguageproject.alutiiqdictionary;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import static com.alutiiqlanguageproject.alutiiqdictionary.R.color;
import static com.alutiiqlanguageproject.alutiiqdictionary.R.color.greyTrans;

/*
 * this class is to help set up the vocab list adapter with names from
 * the datasource
 */
public class ListNameArrayAdapter extends ArrayAdapter<String> {
	//context of vocablistactivity is requried for setting views properly
	Context context;
	//contains vocab list names
	List<String> names;
	private static final String LOGTAG = "alutiiqDict";
	
	//constructor
	public ListNameArrayAdapter(Context context, List<String> names) {
		super(context, android.R.id.content, names);
		this.context = context;
		this.names = names;
	}

    /**
     * standard override to the getView function in the adaptor class.  It sets up the view of the individual
     * items on the list.
     * @param position - position of item
     * @param convertView
     * @param parent - parent view
     * @return
     */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		//creates view for the listitems
        View view = vi.inflate(R.layout.listitem_vocab, null);

        //Just some frilly graphics in order to allow the User to see the borders between each
        // listItem view
        if((position % 2) == 0) {
            view.setBackgroundColor(Color.parseColor("#4DBABABA"));
        }
        else {
             view.setBackgroundColor(Color.parseColor("#4DF2F2F2"));
        }
        //sgets up name for each entry
        String name = names.get(position);
        //sets up where the name will go on the list item
        TextView english = (TextView) view.findViewById(R.id.listName);
        //sets english name
        english.setText(name);
        Log.i(LOGTAG, "adapterreturning view");
        return view;
	}
}
