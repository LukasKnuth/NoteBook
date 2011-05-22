package org.knuth.notebook;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
        ListView ls = this.getListView();
        ls.setEmptyView(empty);
        // Regestry for Context-Menu:
        this.registerForContextMenu(ls);
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
        	Cursor c = db.rawQuery("SELECT headline, strftime(?, edit_date) as 'date', id as '_id' " +
    				"FROM entry " +
        			"ORDER BY edit_date DESC", 
        			new String[] {this.getString(R.string.date_format)});
        	final ListAdapter noteAdapter = new SimpleCursorAdapter(
        			this, 
        			android.R.layout.simple_list_item_2, c, 
        			new String[] {"headline", "date"}, 
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
    	// Intent:
    	Intent i = new Intent(this, DisplayNote.class);
    	i.putExtra("entry_id", id );
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
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenu.ContextMenuInfo menuInfo){
    	// Load the Context Menu:
    	this.getMenuInflater().inflate(R.menu.main_longpress_menu, menu);
    	super.onCreateContextMenu(menu, v, menuInfo);
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
    
    /**
     * Neither deletes or shows the selected Entry.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item){
    	// Get the Selected ID:
    	final AdapterView.AdapterContextMenuInfo info = 
    		(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    	Log.d("OnlyLog", "ID: "+info.id);
    	// Do your Actions:
    	switch (item.getItemId()){
    	case R.id.main_longpress_show:
    		// Show the Note
    		Intent show = new Intent(this, DisplayNote.class);
        	show.putExtra("entry_id", info.id);
        	this.startActivity(show);
    		return true;
    	case R.id.main_longpress_edit:
    		// Edit the Note
    		Intent edit = new Intent(this, EditNote.class);
    		edit.putExtra("entry_id", info.id);
    		this.startActivity(edit);
    		return true;
    	case R.id.main_longpress_delete:
    		// Delete the Note
    		this.longpressDelete(info.id);
    		return true;
    	}
    	return super.onContextItemSelected(item);
    }
    
    /**
     * Called by the Long-press Menu to delete a Note
     * @param note_id The ID of the Note that should be deleted.
     */
    private void longpressDelete(final long note_id){
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
				        	del_curr.bindLong(1, note_id);
				        	del_curr.execute();
				        	db.close();
				        	// Refresh the ListView
				        	Main.this.listNotes();
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
    }
    
}