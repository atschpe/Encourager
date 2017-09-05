package com.example.android.encourager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.encourager.data.EncoContract.EncoEntry;

/**
 * Manage database creation and versions.
 */

public class EncoDbHelper extends SQLiteOpenHelper {

    //for log messages
    public static final String LOG_TAG = EncoDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "accomplishments.db";

    private static final int DATABASE_VERSION = 1; //increment if changes are made to the schema.

    /**
     * Constructor
     *
     * @param context of the app
     */
    public EncoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENCO_TABLE = "CREATE TABLE " + EncoEntry.TABLE_NAME + " ("
                + EncoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EncoEntry.COLUMN_TASK + " INTEGER NOT NULL, "
                + EncoEntry.COLUMN_DETAILS + " TEXT, "
                + EncoEntry.COLUMN_STARS + " INTEGER NOT NULL DEFAULT 0, "
                + EncoEntry.COLUMN_COMMENTS + " Text);";

        db.execSQL(SQL_CREATE_ENCO_TABLE); // exceute sql statement
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //no upgrades yet to be considered
    }
}

