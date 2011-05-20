package org.knuth.notebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity displays the full selected note.
 * @author Lukas Knuth
 *
 */
public class DisplayNote extends Activity{
	
	/** The Database Helper-class instance which is used to connect to the SQLite DB */
	private BookDatabase db_con;
	/** The ID of the note which is currently displayed by this Activity */
	private String displayed_id;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_note);
        // Get the ID from the Intend:
        Bundle extras = this.getIntent().getExtras();
        if (extras != null && extras.containsKey("entry_id")){
        	displayed_id = extras.getString("entry_id");
        } else {
        	displayed_id = null;
        	Toast.makeText(getApplicationContext(),
        		this.getString(R.string.intent_error),
        		Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Opens the Database Connection.
     */
    @Override
    public void onStart(){
    	super.onStart();
    	if (displayed_id != null){
    		// Display Content:
    		db_con = new BookDatabase(getApplicationContext());
    		displayContent();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	this.getMenuInflater().inflate(R.menu.display_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Choose to neither delete or edit the currently shown note.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    	case R.id.menu_delete:
    		// Build Dialog:
    		AlertDialog.Builder build = new AlertDialog.Builder(this);
    		build.setMessage(this.getString(R.string.delete_current_dialog))
    			.setCancelable(true)
    			.setPositiveButton(this.getString(R.string.delete_current_dialog_yes),
    					new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Delete Note:
					        	SQLiteDatabase db = db_con.getWritableDatabase();
					        	// Delete:
					        	SQLiteStatement del_curr = db.compileStatement(
					        			"DELETE FROM entry WHERE id = ?");
					        	del_curr.bindString(1, displayed_id);
					        	del_curr.execute();
					        	db.close();
					        	// Go back:
					        	DisplayNote.this.finish();
							}
						})
				.setNegativeButton(this.getString(R.string.delete_current_dialog_no),
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Kill Dialog:
								dialog.cancel();
							}
						});
    		// Show Dialog:
    		AlertDialog dialog = build.create();
    		dialog.show();
    		return true;
    	case R.id.menu_edit:
    		// Edit...
    		Intent i = new Intent(this, EditNote.class);
    		i.putExtra("entry_id", displayed_id);
    		this.startActivity(i);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    /**
     * Reads the content from the Database and displays it
     */
    private void displayContent(){
    	// Get Views:
    	TextView headline = (TextView) this.findViewById(R.id.display_headline);
    	TextView content = (TextView) this.findViewById(R.id.display_content);
    	// Read:
    	SQLiteDatabase db = db_con.getReadableDatabase();
    	Cursor c = null;
    	try {
        	c = db.rawQuery(
        			"SELECT headline, content " +
        			"FROM entry " +
        			"WHERE id = ?",
        			new String[] {displayed_id}
        		);
        	// Set values:
        	if (c.moveToNext()){
        		headline.setText(c.getString(0));
            	content.setText(c.getString(1));
        	}
    	} finally {
    		// Close Cursor:
    		c.close();
    		db.close();
    	}
    }
    
    /**
     * Closes the Database Connection.
     */
    @Override
    protected void onPause(){
    	db_con.close();
    	super.onPause();
    }

}
