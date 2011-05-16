package org.knuth.notebook;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditNote extends Activity{
	
	private BookDatabase db_con;
	private String edit_id;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.setContentView(R.layout.edit_note);
    	db_con = new BookDatabase(getApplicationContext());
    	// Get the ID from Intent:
    	Bundle extras = this.getIntent().getExtras();
        if (extras != null && extras.containsKey("entry_id")){
        	edit_id = extras.getString("entry_id");
        	displayEdit();
        } else {
        	Toast.makeText(getApplicationContext(),
        		this.getString(R.string.intent_error),
        		Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Save Edited Note
     * @param view
     */
    public void saveEdit(View view){
    	// Get Input:
    	final EditText headline = (EditText) this.findViewById(R.id.edit_headline);
    	String headline_str = headline.getText().toString();
    	final EditText content = (EditText) this.findViewById(R.id.edit_content);
    	String content_str = content.getText().toString(); 
    	if (headline_str.length() > 0 && content_str.length() > 0){
    		// Save changes to Database:
    		SQLiteDatabase db = db_con.getWritableDatabase();
    		SQLiteStatement update_note = db.compileStatement(
    				"UPDATE entry SET headline = ?, content = ? WHERE id = ?");
    		update_note.bindString(1, headline_str);
    		update_note.bindString(2, content_str);
    		update_note.bindString(3, edit_id);
    		// Execute:
    		update_note.execute();
    		db.close();
    		// Go back:
    		this.finish();
    	} else {
    		Toast.makeText(this, "Fill out all Fields!", Toast.LENGTH_SHORT).show();
    	}
    }
    
    /**
     * Dismiss Edit
     * @param view
     */
    public void cancelEdit(View view){
    	// Kill self and return...
    	this.finish();
    }
    
    private void displayEdit(){
    	// Get the EditText-Views:
    	EditText headline = (EditText) this.findViewById(R.id.edit_headline);
    	EditText content = (EditText) this.findViewById(R.id.edit_content);
    	// Get Content:
    	SQLiteDatabase db = db_con.getReadableDatabase();
    	Cursor c = db.rawQuery("SELECT headline, content FROM entry WHERE id = ?",
    			new String[] {edit_id});
    	// Fill Views:
    	if (c.moveToNext() ){
    		headline.setText(c.getString(0));
    		content.setText(c.getString(1));
    	}
    	c.close();
    	db.close();
    }
    
    @Override
    protected void onPause(){
    	db_con.close();
    	super.onPause();
    }

}
