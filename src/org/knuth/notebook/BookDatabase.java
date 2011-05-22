package org.knuth.notebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This is a Database Helper class, which creates and updates the SQLite Database.
 * @author Lukas Knuth
 *
 */
public class BookDatabase extends SQLiteOpenHelper{
	
	/** The Database-File which contains the SQLite Database. */
	private final static String DATABASE_NAME = "notebook.db";
	/** The Database-Version. Determines if the Database get's updated or not. */
	private final static int DATABASE_VERSION = 3;

	/**
	 * Creates the Database Helper class.
	 * @param context The Application-Context.
	 */
	public BookDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Creates the Database tables.
	 * @param db The SQLite Database to send the Query's to.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table entry(" +
				"headline TEXT," +
				"content TEXT," +
				"edit_date DATE," +
				"id INTEGER PRIMARY KEY AUTOINCREMENT" +
				")");		
	}

	/**
	 * Updates the Database if necessary.
	 * @param db The SQLite Database to send the Query's to.
	 * @param old_version The Version-number of the old Database.
	 * @param new_version The Version-number of the new Database.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
		db.execSQL("DROP TABLE entry");
		onCreate(db);
	}

}
