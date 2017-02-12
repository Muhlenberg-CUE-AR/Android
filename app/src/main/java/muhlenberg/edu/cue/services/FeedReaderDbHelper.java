package muhlenberg.edu.cue.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static muhlenberg.edu.cue.services.FeedReaderContract.FeedEntry.COLUMN_NAME_ACTIVATION_RANGE;
import static muhlenberg.edu.cue.services.FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE;
import static muhlenberg.edu.cue.services.FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE;
import static muhlenberg.edu.cue.services.FeedReaderContract.FeedEntry.COLUMN_NAME_LONG_DESC;
import static muhlenberg.edu.cue.services.FeedReaderContract.FeedEntry.COLUMN_NAME_NAME;
import static muhlenberg.edu.cue.services.FeedReaderContract.FeedEntry.COLUMN_NAME_SHORT_DESC;
import static muhlenberg.edu.cue.services.FeedReaderContract.FeedEntry.TABLE_NAME;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_NAME + " TEXT," +
                    COLUMN_NAME_SHORT_DESC + " TEXT," +
                    COLUMN_NAME_LONG_DESC + " TEXT," +
                    COLUMN_NAME_LATITUDE + " NUM," +
                    COLUMN_NAME_LONGITUDE + " NUM)" +
                    COLUMN_NAME_ACTIVATION_RANGE + " NUM," ;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
