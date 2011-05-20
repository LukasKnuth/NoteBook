package org.knuth.notebook;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity which allowes the user to create a new Note.
 * @author Lukas Knuth
 *
 */
public class NewNote extends Activity{
	
	/** The Database Helper-class instance which is used to connect to the SQLite DB */
	private BookDatabase db_con;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);
    }
    
    /**
     * Close the Database-connection.
     */
    @Override
    protected void onPause(){
    	db_con.close();
    	super.onPause();
    }
    
    /**
     * Open the Database-connection.
     */
    @Override
    public void onStart(){
    	super.onStart();
    	db_con = new BookDatabase(getApplicationContext());
    }
    
    /**
     * Called by the Save-Button and saves the note to the Database.
     * @param view The View of the current Activity.
     */
    public void saveNote(View view){
    	// Test if Fields are filled out:
    	final EditText headline = (EditText) this.findViewById(R.id.headline);
    	String headline_str = headline.getText().toString();
    	final EditText content = (EditText) this.findViewById(R.id.content);
    	String content_str = content.getText().toString(); 
    	if (headline_str.length() > 0 && content_str.length() > 0){
    		// In the Database:
    		SQLiteDatabase db = db_con.getWritableDatabase();
    		try {
    			SQLiteStatement inset_new = db.compileStatement(
        				"Insert into entry (headline, content)" +
        				"values (?,?)");
        		// Einsetzen:
        		inset_new.bindString(1, headline_str);
        		inset_new.bindString(2, content_str);
        		// Ausführen:
        		long id = inset_new.executeInsert();
        		// Show the Entry:
        		Intent i = new Intent(getApplicationContext(), DisplayNote.class);
        		i.putExtra("entry_id", id+"");
        		this.startActivity(i);
        		this.finish();
    		} finally {
    			db.close();
    		}
    	} else {
    		Toast.makeText(this, "Fill out all Fields!", Toast.LENGTH_SHORT).show();
    	}
    }

}
