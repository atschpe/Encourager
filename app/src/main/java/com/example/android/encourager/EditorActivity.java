package com.example.android.encourager;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.encourager.data.EncoContract.EncoEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int EXISITNG_ENCO_LOADER = 0; // identifier for loader

    //Views to populate
    @BindView(R.id.task_spinner)
    Spinner task_spin;
    @BindView(R.id.details_edittext)
    EditText detail_editText;
    @BindView(R.id.stars_spinner)
    Spinner star_spin;
    @BindView(R.id.comments_edittext)
    EditText comment_editText;

    //spinners
    private int task = EncoEntry.TASK_CHORE;
    private int star = EncoEntry.STARS_1;

    private Uri currentAccomplishmentUri; //Incoming Uri to select update over create function.
    private boolean encoHasChanged = false; //Keep track of whether an item is new or to be updated.

    //Register any updates made by the user.
    View.OnTouchListener touched = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            encoHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ButterKnife.bind(this); //initialise views

        // set touch listener to all views.
        task_spin.setOnTouchListener(touched);
        detail_editText.setOnTouchListener(touched);
        star_spin.setOnTouchListener(touched);
        comment_editText.setOnTouchListener(touched);


        Intent receivingData = getIntent(); //get data from the item clicked on in the MainActivity
        currentAccomplishmentUri = receivingData.getData();

        if (currentAccomplishmentUri == null) {
            setTitle(getString(R.string.new_accomplishment)); //set Editor title to new
            invalidateOptionsMenu();//no options menu needed
        } else {
            setTitle(getString(R.string.edit_accomplishment)); // set Editor title to edit
            getLoaderManager().initLoader(EXISITNG_ENCO_LOADER, null, this);//retrieve&display data
        }

        setupSpinners();
    }

    /**
     * Setup the spinners for stars and task
     */
    private void setupSpinners() {

        //Setup the task-spinner
        ArrayAdapter taskSpinner = ArrayAdapter.createFromResource(this, R.array.task_list,
                android.R.layout.simple_spinner_item);
        taskSpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        task_spin.setAdapter(taskSpinner);
        task_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        task = EncoEntry.TASK_CHORE;
                        break;
                    case 1:
                        task = EncoEntry.TASK_HELPED;
                        break;
                    case 2:
                        task = EncoEntry.TASK_HOMEWORK;
                        break;
                    case 3:
                        task = EncoEntry.TASK_TIDIED_UP;
                        break;
                    case 4:
                        task = EncoEntry.TASK_OTHER;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                task = EncoEntry.TASK_CHORE;

            }
        });

        //Setup the star-spinner
        ArrayAdapter starSpinner = ArrayAdapter.createFromResource(this, R.array.star_list,
                android.R.layout.simple_spinner_item);
        starSpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        star_spin.setAdapter(starSpinner);
        star_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        star = EncoEntry.STARS_1;
                        break;
                    case 1:
                        star = EncoEntry.STARS_2;
                        break;
                    case 2:
                        star = EncoEntry.STARS_3;
                        break;
                    case 3:
                        star = EncoEntry.STARS_4;
                        break;
                    case 4:
                        star = EncoEntry.STARS_5;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                star = EncoEntry.STARS_1;
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { //Project to retrieve all columns
                EncoEntry._ID, EncoEntry.COLUMN_TASK, EncoEntry.COLUMN_DETAILS,
                EncoEntry.COLUMN_STARS, EncoEntry.COLUMN_COMMENTS};

        //create new cursor laoder, with the current accomplishment and above projection.
        return new CursorLoader(this, currentAccomplishmentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return; //return early if cursor is empty
        }
        cursor.moveToFirst(); // move to the first&only row of the cursor

        //retrieve the columns from the table
        int taskColumn = cursor.getColumnIndex(EncoEntry.COLUMN_TASK);
        int detailsColumn = cursor.getColumnIndex(EncoEntry.COLUMN_DETAILS);
        int starsColumn = cursor.getColumnIndex(EncoEntry.COLUMN_STARS);
        int commentsColumn = cursor.getColumnIndex(EncoEntry.COLUMN_COMMENTS);

        //Extract the data.
        int taskData = cursor.getInt(taskColumn);
        String detailsData = cursor.getString(detailsColumn);
        int starsData = cursor.getInt(starsColumn);
        String commentsData = cursor.getString(commentsColumn);

        //Populate the views with the data.
        detail_editText.setText(detailsData);
        comment_editText.setText(commentsData);

        switch (taskData) {
            case EncoEntry.TASK_CHORE:
                task_spin.setSelection(0);
                break;
            case EncoEntry.TASK_HELPED:
                task_spin.setSelection(1);
                break;
            case EncoEntry.TASK_HOMEWORK:
                task_spin.setSelection(2);
                break;
            case EncoEntry.TASK_TIDIED_UP:
                task_spin.setSelection(3);
                break;
            case EncoEntry.TASK_OTHER:
                task_spin.setSelection(4);
                break;
        }

        switch (starsData) {
            case EncoEntry.STARS_1:
                star_spin.setSelection(0);
                break;
            case EncoEntry.STARS_2:
                star_spin.setSelection(1);
                break;
            case EncoEntry.STARS_3:
                star_spin.setSelection(2);
                break;
            case EncoEntry.STARS_4:
                star_spin.setSelection(3);
                break;
            case EncoEntry.STARS_5:
                star_spin.setSelection(4);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { //Reset all fields to neutral on loader reset
        detail_editText.setText("");
        comment_editText.setText("");
        task_spin.setSelection(0);
        star_spin.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * hide delete button when user adds a new accomplishment
     *
     * @param menu is the options provided in the action bar.
     * @return altered menu if user is adding a new accomplishmnet
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentAccomplishmentUri == null) {
            MenuItem mi = menu.findItem(R.id.editor_delete);
            mi.setVisible(false);
        }
        return true;
    }

    /**
     * Link menu items to their methods
     *
     * @param item the user clicks
     * @return action/method link to item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editor_save:
                saveAccomplishment(); //save accomplishment
                finish(); // return to main activity
                return true;
            case R.id.editor_delete:
                deleteConfirmationDialog();//ensure the users wants to delete the accomplishment
                return true;
            case android.R.id.home:
                if (!encoHasChanged) { // if user has not changed anything
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                } else {
                    DialogInterface.OnClickListener discard = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    };
                    unsavedChangesDialog(discard);//Show dialog warning about discarding changes
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * action when users presses back
     */
    @Override
    public void onBackPressed() {
        if (!encoHasChanged) { // if there are no changes
            super.onBackPressed();
            return;
        } else { // if there are unsaved changes
            DialogInterface.OnClickListener discard = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            };
            unsavedChangesDialog(discard); //open dialog for unsaved changes.
        }
    }

    /**
     * Prompt user to confirm discarding the changes made
     *
     * @param discard is the onclickListener called to discard the changes.
     */
    private void unsavedChangesDialog(DialogInterface.OnClickListener discard) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialog);
        alert.setMessage(R.string.discard_query);
        alert.setPositiveButton(R.string.discard, discard);
        alert.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();//dismiss dialog and return to the editor
                }
            }
        });
        AlertDialog alertDialog = alert.create(); // create & show dialog
        alertDialog.show();
    }

    /**
     * Prompt user to confirm deleting the accomplishment
     */
    private void deleteConfirmationDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialog);
        alert.setMessage(R.string.delete_query);
        alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccomplishment(); //call delete method
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss(); //dismiss dialog and return to the editor
                }
            }
        });

        AlertDialog alertDialog = alert.create(); // create & show dialog
        alertDialog.show();
    }

    /**
     * delete accomplishment from the database
     */
    private void deleteAccomplishment() {
        if (currentAccomplishmentUri != null) {
            int itemAffected = getContentResolver().delete(currentAccomplishmentUri, null, null);
            if (itemAffected == 0) {
                Toast.makeText(this, R.string.delete_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.delete_successful, Toast.LENGTH_SHORT).show();
            }
        }
        finish();//return to main activity
    }

    /**
     * Save/Update the accomplishment
     */
    public void saveAccomplishment() {
        //retrieve user entries from the editText views.
        String detailsText = detail_editText.getText().toString().trim();
        String commentsText = comment_editText.getText().toString().trim();

        //ensure this is a new accomplishment and user has not added any information
        if (currentAccomplishmentUri == null
                && TextUtils.isEmpty(detailsText) && TextUtils.isEmpty(commentsText)
                && task == EncoEntry.TASK_CHORE && star == EncoEntry.STARS_1) {
            return; // return early as there have been no alterations
        }

        ContentValues values = new ContentValues();
        values.put(EncoEntry.COLUMN_TASK, task); //put in "task" value

        if (TextUtils.isEmpty(detailsText)) { // put in "details" value
            values.put(EncoEntry.COLUMN_DETAILS, "");
        } else {
            values.put(EncoEntry.COLUMN_DETAILS, detailsText);
        }

        values.put(EncoEntry.COLUMN_STARS, star); // put in "star" value

        if (TextUtils.isEmpty(commentsText)) { // put in "comments" value
            values.put(EncoEntry.COLUMN_COMMENTS, "");
        } else {
            values.put(EncoEntry.COLUMN_COMMENTS, commentsText);
        }

        //If this is a new accomplishment, send its uri to the provider
        if (currentAccomplishmentUri == null) {
            Uri newEnco = getContentResolver().insert(EncoEntry.CONTENT_URI, values);

            //Toast message whether the save was successful or not
            if (newEnco == null) {
                Toast.makeText(this, R.string.error_save, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.successful_save, Toast.LENGTH_SHORT).show();
            }
            //If this is an exisiting accomplishment, send uri to update it based on the id provided
        } else {
            int itemAffected = getContentResolver().update(currentAccomplishmentUri, values, null, null);

            //Toast message whether the sace was successful or not
            if (itemAffected == 0) {
                Toast.makeText(this, R.string.error_update, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.successful_udpate, Toast.LENGTH_SHORT).show();
            }
        }
    }
}