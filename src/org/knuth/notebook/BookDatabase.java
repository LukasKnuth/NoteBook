package org.knuth.notebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDatabase extends SQLiteOpenHelper{
	
	private final static String DATABASE_NAME = "notebook.db";
	private final static int DATABASE_VERSION = 2;

	public BookDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Create the Database-Tables:
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table entry(" +
				"headline TEXT," +
				"content TEXT," +
				"id INTEGER PRIMARY KEY AUTOINCREMENT" +
				")");		
	}

	/**
	 * Update the Database-Schema:
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
		db.execSQL("DROP TABLE entry");
		onCreate(db);
	}

}
