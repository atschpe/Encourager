package com.example.android.encourager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.encourager.data.EncoContract.EncoEntry;

/**
 * {@link EncoProvider} is a {@link ContentProvider} for the Encourager app.
 */

public class EncoProvider extends ContentProvider {

    public static final String LOG_TAG = EncoProvider.class.getSimpleName(); // for log messages.
    private static final int ENCO_LIST = 100; // Uri matcher code for the full table
    private static final int ENCO_ID = 101; // uri matcher code for a single item

    /**
     * Match content URI to its corresponding code. This static initialiser is called by addURI().
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // When calling multiple rows from the table.
        uriMatcher.addURI(EncoContract.CONTENT_AUTHORITY, EncoContract.PATH_ENCO, ENCO_LIST);

        // When calling a single item. The wildcard "#" is a place holder for the item id.
        uriMatcher.addURI(EncoContract.CONTENT_AUTHORITY, EncoContract.PATH_ENCO + "/#", ENCO_ID);
    }

    private EncoDbHelper encoDbHelper; // Database helper.

    @Override
    public boolean onCreate() {
        encoDbHelper = new EncoDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase sqlDb = encoDbHelper.getReadableDatabase(); //request readable access.
        Cursor cursor; //cursor to hold the result.

        //match uri to specific code.
        int match = uriMatcher.match(uri);
        switch (match) {

            // query directly with provided information. May contain multiple rows.
            case (ENCO_LIST):
                cursor = sqlDb.query(EncoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            //Extract specific item id from uri and replace all "?" in the query with it.
            case (ENCO_ID):
                selection = EncoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqlDb.query(EncoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown uri" + uri);
        }

        // Set notification to indicate what content was queried, or if it it needs to be updated.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case (ENCO_LIST):
                return EncoEntry.CONTENT_LIST_TYPE;
            case (ENCO_ID):
                return EncoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown uri " + uri + "with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case (ENCO_LIST):
                return insertEnco(uri, values);
            default:
                throw new IllegalArgumentException(uri + " cannot be inserted.");
        }
    }

    /**
     * Insert new item into the database based on the provided values.
     *
     * @param uri    is the incoming request.
     * @param values are provided to be added to the database for this item.
     * @return uri for the row.
     */
    private Uri insertEnco(Uri uri, ContentValues values) {

        //The values that need to be checked.
        Integer task = values.getAsInteger(EncoEntry.COLUMN_TASK);
        Integer stars = values.getAsInteger(EncoEntry.COLUMN_STARS);

        if (task == null || !EncoEntry.isValidTask(task)) { // Ensure task is not null
            throw new IllegalArgumentException("Item requires a selected task.");
        }
        if (stars == null || !EncoEntry.isValidStars(stars)) { // Ensure stars is not null
            throw new IllegalArgumentException("Item requires a selected star value.");
        }

        // details and comments can be left blank.

        SQLiteDatabase sqlDb = encoDbHelper.getWritableDatabase(); // request writable database.

        long id = sqlDb.insert(EncoEntry.TABLE_NAME, null, values); // insert new item.
        if (id == -1) { // if id =-1 an error occured.
            Log.e(LOG_TAG, "Row could not be inserted for " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null); //notify all listeners.

        return ContentUris.withAppendedId(uri, id); // return uri with appended id of new row.
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDb = encoDbHelper.getWritableDatabase(); // request writable database.

        int rowsDeleted; //keep track of row ids.

        final int match = uriMatcher.match(uri);
        switch (match) {
            case (ENCO_LIST): // delete all rows
                rowsDeleted = sqlDb.delete(EncoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case (ENCO_ID): // delete item at given id.
                selection = EncoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = sqlDb.delete(EncoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(uri + " could not be deleted");
        }

        if (rowsDeleted != 0) { //notify all listeners if anything is deleted.
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted; // return the number of rows that have been deleted.
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //check whether values are present
        if (values.containsKey(EncoEntry.COLUMN_TASK)) {
            Integer task = values.getAsInteger(EncoEntry.COLUMN_TASK);
            if (task == null && EncoEntry.isValidTask(task)) {
                throw new IllegalArgumentException("Item requires selected task.");
            }
        }

        if (values.containsKey(EncoEntry.COLUMN_STARS)) {
            Integer stars = values.getAsInteger(EncoEntry.COLUMN_STARS);
            if (stars == null && EncoEntry.isValidStars(stars)) {
                throw new IllegalArgumentException("Item requires selected star value.");
            }
        }

        //"details" and "comments" can be blank, as these are not required.

        if (values.size() == 0) { //if there are no values to update than do nothing in the database.
            return 0;
        }

        SQLiteDatabase sqlDb = encoDbHelper.getWritableDatabase(); // request writable database

        //update the database and return id of the affected rows.
        int rowsUpdated = sqlDb.update(EncoEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) { //notify all listeners if anything is updated.
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated; // return the number of rows that have been updated.
    }
}