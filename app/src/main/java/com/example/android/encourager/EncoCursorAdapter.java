package com.example.android.encourager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.encourager.data.EncoContract.EncoEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link EncoCursorAdapter} extends the {@link CursorAdapter} to enable the cursor to populate the
 * list view.
 */
public class EncoCursorAdapter extends CursorAdapter {

    @BindView(R.id.task)
    TextView task;
    @BindView(R.id.stars)
    ImageView stars;
    @BindView(R.id.comment)
    TextView comment;

    public static final String LOG_TAG = EncoCursorAdapter.class.getSimpleName();

    /**
     * the constructor
     *
     * @param ctxt   is the activity
     * @param cursor retrieves data to be displayed.
     */
    public EncoCursorAdapter(Context ctxt, Cursor cursor) {
        super(ctxt, cursor, 0);
    }

    /**
     * Make a new listview ready for populating
     *
     * @param ctxt   is the activity
     * @param cursor retrieves the data to be displayed
     * @param parent is where the listview is located.
     * @return the empy listview ready for use.
     */
    @Override
    public View newView(Context ctxt, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(ctxt).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context ctxt, Cursor cursor) {

        ButterKnife.bind(this, view); //initialise the views

        //Locate columns needed to populate the views
        int taskColumnIndex = cursor.getColumnIndex(EncoEntry.COLUMN_TASK);
        int detailsColumnIndex = cursor.getColumnIndex(EncoEntry.COLUMN_DETAILS);
        int starsColumnIndex = cursor.getColumnIndex(EncoEntry.COLUMN_STARS);
        int commentColumnIndex = cursor.getColumnIndex(EncoEntry.COLUMN_COMMENTS);

        //retrieve the information from the table
        int taskInput = cursor.getInt(taskColumnIndex);
        String detailsInput = cursor.getString(detailsColumnIndex);
        int starsInput = cursor.getInt(starsColumnIndex);
        String commentInput = cursor.getString(commentColumnIndex);

        //populate views with retrieved information: task
        String taskSum = null;
        String detailSum;
        if (detailsInput == null) {
            detailSum = "";
        } else {
            detailSum = ctxt.getString(R.string.colon)
                    + detailsInput;
        }
        switch (taskInput) {
            case (EncoEntry.TASK_CHORE):
                taskSum = ctxt.getString(R.string.chore) + detailSum;
                break;
            case (EncoEntry.TASK_HELPED):
                taskSum = ctxt.getString(R.string.helped) + detailSum;
                break;
            case (EncoEntry.TASK_HOMEWORK):
                taskSum = ctxt.getString(R.string.homework) + detailSum;
                break;
            case (EncoEntry.TASK_TIDIED_UP):
                taskSum = ctxt.getString(R.string.tidied) + detailSum;
                break;
            case (EncoEntry.TASK_OTHER):
                taskSum = detailsInput;
        }
        task.setText(taskSum);

        //populate: stars
        int starDisplay = 0;
        switch (starsInput) {
            case (EncoEntry.STARS_1):
                starDisplay = R.drawable.star_one;
                break;
            case (EncoEntry.STARS_2):
                starDisplay = R.drawable.star_two;
                break;
            case (EncoEntry.STARS_3):
                starDisplay = R.drawable.star_three;
                break;
            case (EncoEntry.STARS_4):
                starDisplay = R.drawable.star_four;
                break;
            case (EncoEntry.STARS_5):
                starDisplay = R.drawable.star_five;
        }
        stars.setImageResource(starDisplay);

        //populate: comment - if available
        if (commentInput == null) {
            comment.setVisibility(View.GONE);
        } else {
            comment.setText(commentInput);
        }
    }
}