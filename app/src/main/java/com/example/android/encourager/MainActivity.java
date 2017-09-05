package com.example.android.encourager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.encourager.data.EncoContract.EncoEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Display list of accomplished tasks and the current tally.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.listview)
    ListView view;
    @BindView(R.id.emptyView)
    TextView emptyView;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static int ENCO_LOADER = 1; // Identifier for loader.
    EncoCursorAdapter encoCursorAdapter; //Adapter for the list view.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this); // initiate all Views.

        fab.setOnClickListener(new View.OnClickListener() { // go to Editor Activity
            @Override
            public void onClick(View v) {
                Intent editorNewItem = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(editorNewItem);
            }
        });

        view.setEmptyView(emptyView); // if there are no items to display

        encoCursorAdapter = new EncoCursorAdapter(this, null);//initiate adapter and set to view.
        view.setAdapter(encoCursorAdapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {//click item to open editor
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editorUpdateItem = new Intent(MainActivity.this, EditorActivity.class);

                //Append id of specific item clicked on, enabling the cursor to retrieve the
                //corresponding information to be displayed.
                Uri contentEncoUri = ContentUris.withAppendedId(EncoEntry.CONTENT_URI, id);

                editorUpdateItem.setData(contentEncoUri); //send uri along with the intent
                startActivity(editorUpdateItem);
            }
        });

        getLoaderManager().initLoader(ENCO_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = { //define the projection accessing the necessary table rows
                EncoEntry._ID,
                EncoEntry.COLUMN_TASK,
                EncoEntry.COLUMN_DETAILS,
                EncoEntry.COLUMN_STARS,
                EncoEntry.COLUMN_COMMENTS};
        return new CursorLoader(this, EncoEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        encoCursorAdapter.swapCursor(data); //Update adapter with the new data from the cursor
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        encoCursorAdapter.swapCursor(null); //whenever all data needs to be deleted
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_dummy: //when user clicks on "Add Dummy"
                insertDummyEnco();
                return true;
            case R.id.delete_all: //when user clicks on "delete all"
                deleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to add dummy
     */
    private void insertDummyEnco() {

        ContentValues dummyValue = new ContentValues(); //create a dummy value
        dummyValue.put(EncoEntry.COLUMN_TASK, 0);
        dummyValue.put(EncoEntry.COLUMN_STARS, 2);

        //insert dummy value as row into database
        Uri newEnco = getContentResolver().insert(EncoEntry.CONTENT_URI, dummyValue);
    }

    /**
     * Helper method to delete all items
     */
    private void deleteAll() {
        int rowsDeleted = getContentResolver().delete(EncoEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, "Number of rows deleted: " + rowsDeleted);
    }
}