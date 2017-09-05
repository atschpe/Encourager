package com.example.android.encourager.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * {@link EncoContract} holds all constants needed for the SQLite.
 */

public class EncoContract {
    private EncoContract() {
    }

    //global variables for content and path
    public static final String CONTENT_AUTHORITY = "com.example.android.encourager";
    public static final Uri BASE_URI_CONTENT = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ENCO = "encourager";

    //Class to define encourager data
    public static final class EncoEntry implements BaseColumns {

        //MIME type for list
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + PATH_ENCO;

        //MIME type for single item
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + PATH_ENCO;

        //global variable for content uri.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI_CONTENT, PATH_ENCO);

        public static final String TABLE_NAME = "encourager"; //table name: Encourager
        public static final String _ID = BaseColumns._ID; // unique id for each item

        //columns
        public static final String COLUMN_TASK = "task";
        public static final String COLUMN_DETAILS = "details";
        public static final String COLUMN_STARS = "stars"; // Choice of 1-5 stars to be awarded
        public static final String COLUMN_COMMENTS = "comments";

        //Selectables
        public static final int TASK_TIDIED_UP = 0;
        public static final int TASK_HOMEWORK = 1;
        public static final int TASK_CHORE = 2;
        public static final int TASK_HELPED = 3;
        public static final int TASK_OTHER = 4;

        public static final int STARS_1 = 0;
        public static final int STARS_2 = 1;
        public static final int STARS_3 = 2;
        public static final int STARS_4 = 3;
        public static final int STARS_5 = 4;

        //Ensure "task" column is valid
        public static boolean isValidTask(int task) {
            if (task == TASK_TIDIED_UP || task == TASK_HOMEWORK || task == TASK_CHORE ||
                    task == TASK_HELPED || task == TASK_OTHER) {
                return true;
            }
            return false;
        }

        //Ensure stars" column is valid
        public static boolean isValidStars(int stars) {
            if (stars == STARS_1 || stars == STARS_2 || stars == STARS_3 || stars == STARS_4 ||
                    stars == STARS_5) {
                return true;
            }
            return false;
        }
    }
}