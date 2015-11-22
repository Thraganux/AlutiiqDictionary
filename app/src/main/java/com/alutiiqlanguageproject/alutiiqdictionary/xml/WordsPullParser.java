package com.alutiiqlanguageproject.alutiiqdictionary.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.Resources.NotFoundException;

import android.util.Log;

import com.alutiiqlanguageproject.alutiiqdictionary.model.Word;
import com.alutiiqlanguageproject.alutiiqdictionary.R;


public class WordsPullParser {

	
	private static final String wordId = "wordId";
	private static final String english = "english";
	private static final String aluSouth = "aluSouth";
	private static final String aluNorth = "aluNorth";
	private static final String function = "function";
	private static final String category = "category";
	private static final String comments = "comments";
	
	private Word currentWord = null;
	private String currentTag  = null;
	List<Word> words = new ArrayList<Word>();
	
	private static final String LOGTAG = "alutiiqDict";
	
	public WordsPullParser() {
		// TODO Auto-generated constructor stub
	}
	
	public List<Word> parseXML(Context context) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			
			InputStream stream = context.getResources().openRawResource(R.raw.words);
			xpp.setInput(stream, null);

			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					handleStartTag(xpp.getName());
				} else if (eventType == XmlPullParser.END_TAG) {
					currentTag = null;
				} else if (eventType == XmlPullParser.TEXT) {
					handleText(xpp.getText());
				}
				eventType = xpp.next();
			}
		
		} catch (NotFoundException e) {
			Log.d(LOGTAG, e.getMessage());
		} catch (XmlPullParserException e) {
			Log.d(LOGTAG, e.getMessage());
		} catch (IOException e) {
			Log.d(LOGTAG, e.getMessage());
		}

		return words;
	}
	
	private void handleText(String text) {
		String xmlText = text;
		if (currentWord != null && currentTag != null) {
			if (currentWord.equals(wordId)) {
				Integer id = Integer.parseInt(xmlText);
				currentWord.setWordId(id);
			} 
			else if (currentTag.equals(english)) {
				currentWord.setEnglish(xmlText);
			}
			else if (currentTag.equals(aluSouth)) {
				currentWord.setAluSouth(xmlText);
			}
			else if (currentTag.equals(aluNorth)) {
				currentWord.setAluNorth(xmlText);
			}
			else if (currentTag.equals(function)) {
				currentWord.setFunction(xmlText);
			}
			else if (currentTag.equals(category)) {
				currentWord.setCategory(xmlText);
			}
			else if (currentTag.equals(comments)) {
				currentWord.setComments(xmlText);
			}
		}
	}

	private void handleStartTag(String name) {
		if (name.equals("word")) {
			currentWord = new Word();
			words.add(currentWord);
		}
		else {
			currentTag = name;
		}
	}

}
