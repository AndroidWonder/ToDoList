package com.homework;

import java.util.ArrayList;
import java.util.Date;
import java.io.*;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.util.Log;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.util.Locale;


public class ToDoList extends ListActivity implements OnInitListener{
	
	private EditText text;
	private ListView list;
	//private TextView tview;
	private String fileName = "list.txt";
	private TextToSpeech speaker;
	private static final String tag = "Widgets";
	
	ArrayList<String> arrOptions;
	ArrayAdapter<String> adaOptions;
	int itemSelected;   //number of list item selected
	int numItems = 0;   //number of items on list
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        text = (EditText)findViewById(R.id.EditText01);       
        text.setHint("Enter Text Here ...");
        
      //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);
        
        arrOptions = new ArrayList<String>();
        adaOptions = new ArrayAdapter<String>(this, R.layout.main1, arrOptions);
        
        setListAdapter(adaOptions);
     
        //check if the list is to be initialized from last session
        String path = getFilesDir().toString();
        if (new File(path + "/" + fileName).exists()) {      	
        readFile();
        }
       
    }
    
    // Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // If a language is not be available, the result will indicate it.
            int result = speaker.setLanguage(Locale.US);
           
           //  int result = speaker.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
               // Language data is missing or the language is not supported.
                Log.e(tag, "Language is not available.");
            } else {
                  // The TTS engine has been successfully initialized
            	speak("Welcome to To Do List");
            	Log.i(tag, "TTS Initialization successful.");
            }
        } else {
            // Initialization failed.
            Log.e(tag, "Could not initialize TextToSpeech.");
        }
    }
    
  //speaks the contents of output
    public void speak(String output){
    	speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null);
    }

    
    //create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){	
        getMenuInflater().inflate(R.menu.main, menu);
    	return true;
    }
    
    //select options menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	int id = item.getItemId();
    	
    	switch (id) {
    	case R.id.save: {  //save file
    		saveFile();
    		return true;
    	}
    	case R.id.close:  {  //exit
    		saveFile();
    		finish();
    		}
    	case R.id.add: {  //add new list item
    		String str = text.getText().toString();
    		numItems++;
    		arrOptions.add(numItems + ". " + str);
    		text.setText("");
    		text.setHint("Enter Text Here ...");
    		speak(str + " added");
    		adaOptions.notifyDataSetChanged();
    		return true;
    	   }
    	case R.id.delete: {  //delete list item
    		if (arrOptions.size()==0) return false; //check list not empty
    		String delItem = arrOptions.get(itemSelected);
    		int point = delItem.indexOf(".") + 2;  //don't speak number part
    		speak(delItem.substring(point) + " deleted");
    		arrOptions.remove(itemSelected);
    		numItems--;
    		for (int i=itemSelected; i < arrOptions.size(); i++) {
    			String str = arrOptions.get(i);
    			int index = str.indexOf(".");
    			int k = Integer.parseInt(str.substring(0, index)) - 1;
    			String s = k + str.substring(index);
    			arrOptions.set(i, s);
    		}
    		text.setText("");
    		text.setHint("Enter Text Here ...");
    		adaOptions.notifyDataSetChanged();  		
    		return true;
    	}
    	case R.id.update: {  //update list item
    		if (arrOptions.size()==0) return false; //check list not empty
    		String str = text.getText().toString();    		
    		arrOptions.set(itemSelected, (itemSelected+1) + ". " + str);
    		text.setText("");
    		text.setHint("Enter Text Here ...");
    		adaOptions.notifyDataSetChanged();
    		return true;
    	}
    	
    	default: {return false;}	
    	}
    }
    
    //display list item clicked
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	String str = arrOptions.get(position);
    	int index = str.indexOf(".");  //get rid of item number
    	str = str.substring(index+2);
    	text.setText(str);
    	itemSelected = position;
    
    }
    
    public void saveFile() {
    	try {  //save list to file
			   // look in /data/data/com.homework/list.txt
    		OutputStreamWriter out = new OutputStreamWriter( 
    				openFileOutput(fileName,MODE_PRIVATE));
		for (int i=0; i<arrOptions.size();i++) {
			out.write(arrOptions.get(i) + "\n");
		}
		out.close();
		} catch(IOException e) {Toast.makeText(this, "e.getMessage()", Toast.LENGTH_LONG).show(); }
    }
    
    public void readFile() {
    	try {  //read list from file
    		//open stream for reading
			InputStream in = openFileInput(fileName);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader reader = new BufferedReader(isr);
			String str = null;
			
			while ((str = reader.readLine()) != null) {
				numItems++;
	    		arrOptions.add(str);
	    		adaOptions.notifyDataSetChanged();
			}
			reader.close();
		} catch(IOException e) {Toast.makeText(this, "List Initialized", Toast.LENGTH_LONG).show(); }
    }
}