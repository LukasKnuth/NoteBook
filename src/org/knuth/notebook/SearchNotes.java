package org.knuth.notebook;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * This Activity presents the search-results.
 * @author Lukas Knuth
 * 
 */
public class SearchNotes extends ListActivity{
	
	/** The Database Helper-class instance which is used to connect to the SQLite DB */
	private BookDatabase db_con;
	/** The Search-query passed by the Android Search interface */
	private String query;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display a Note if nothing is in the Database:
        TextView empty = new TextView(this);
        empty.setText(R.string.empty_search_note);
        // IMPORTANT:
        empty.setVisibility(View.VISIBLE);
        ((ViewGroup)this.getListView().getParent()).addView(empty);
        // IMPORTANT END
        this.getListView().setEmptyView(empty);
        // Getting the Search-Query:
        Intent i = this.getIntent();
        if (i.getAction().equals(Intent.ACTION_SEARCH)){
        	query = i.getStringExtra(SearchManager.QUERY);
        }
	}
	
	/**
	 * Searches the Database for the entered Search-Query and presents the results in a ListView.
	 */
	private void searchAndDisplay(){
		SQLiteDatabase db = db_con.getReadableDatabase();
		try {
			Cursor c = db.rawQuery("SELECT headline, strftime(?, edit_date) as 'date', id as '_id' " +
				"FROM entry WHERE " +
				"(headline LIKE ?) OR (content LIKE ?) " +
				"ORDER BY id DESC",
				new String[] {this.getString(R.string.date_format),
					"%"+query+"%", "%"+query+"%"});
			final ListAdapter searchAdapter = new SimpleCursorAdapter(
					this, 
	    			android.R.layout.simple_list_item_2, c, 
	    			new String[] {"headline", "date"}, 
	    			new int[] {android.R.id.text1, android.R.id.text2});
			this.setListAdapter(searchAdapter);
		} finally {
			db.close();
		}
	}
	
	/**
	 * Opens the Database Connection.
	 */
	@Override
    public void onStart(){
    	super.onStart();
    	db_con = new BookDatabase(getApplicationContext());
        searchAndDisplay();
    }
	
	/**
	 * Open the selected Note in the DisplayNote Activity.
	 */
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id){
    	super.onListItemClick(l, v, position, id);
    	// Intent:
    	Intent i = new Intent(this, DisplayNote.class);
    	i.putExtra("entry_id", id);
    	this.startActivity(i);
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
