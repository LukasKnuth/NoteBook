package org.knuth.notebook;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

/**
 * The Main Activity of the Application which shows all available Notes in a ListView.
 * @author Lukas Knuth
 *
 */
public class Main extends ListActivity {
	
	/** The Database Helper-class instance which is used to connect to the SQLite DB */
	private BookDatabase db_con;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display a Note if nothing is in the Database:
        TextView empty = new TextView(this);
        empty.setText(R.string.empty_list_note);
        // IMPORTANT:
        empty.setVisibility(View.VISIBLE);
        ((ViewGroup)this.getListView().getParent()).addView(empty);
        // IMPORTANT END
        this.getListView().setEmptyView(empty);
    }
    
    /**
     * Opens the Database-Connection and calls the population-class
     */
    @Override
    public void onStart(){
    	super.onStart();
    	db_con = new BookDatabase(this);
        listNotes();
    }
    
    /**
     * Gets the Notes from the Database and puts them on the ListView.
     */
    private void listNotes(){
    	SQLiteDatabase db = db_con.getReadableDatabase();
    	try {
        	Cursor c = db.rawQuery("SELECT headline, id as '_id' " +
    				"FROM entry " +
        			"ORDER BY id DESC", null);
        	final ListAdapter noteAdapter = new SimpleCursorAdapter(
        			this, 
        			android.R.layout.simple_list_item_2, c, 
        			new String[] {"headline", "_id"}, 
        			new int[] {android.R.id.text1, android.R.id.text2});
        	this.setListAdapter(noteAdapter);
    	} finally {
    		db.close();
    	}
    }
    
    /**
     * Opens the selected Note in the DisplayNote-Activity.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
    	super.onListItemClick(l, v, position, id);
    	// Double-Row List Item with the Headline and ID:
    	TwoLineListItem curr = (TwoLineListItem) v;
    	TextView curr_line = (TextView) curr.getText2();
    	// Get the Value from the Second TextView:
    	Log.d("OnlyLog", curr_line.getText().toString());
    	// Intent:
    	Intent i = new Intent(this, DisplayNote.class);
    	i.putExtra("entry_id", curr_line.getText().toString() );
    	this.startActivity(i);
    }
    
    /**
     * Closes the Database-Connection.
     */
    @Override
    protected void onPause(){
    	db_con.close();
    	super.onPause();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	this.getMenuInflater().inflate(R.menu.main_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Starts the NewNote-Activity to create a new note.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    	case R.id.menu_create:
    		// Neuer Eintrag:
    		Intent i = new Intent(getApplicationContext(), NewNote.class);
    		this.startActivity(i);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
}