package muhlenberg.edu.cue.services.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX1;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX2;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX3;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX4;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.COLUMN_NAME_LATITUDE;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONGITUDE;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONG_DESC;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.COLUMN_NAME_NAME;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.COLUMN_NAME_SHORT_DESC;
import static muhlenberg.edu.cue.services.database.CUEDatabaseContract.FeedEntry.TABLE_NAME;

public class CUEDatabaseHelper extends SQLiteOpenHelper {
    /*
        Defines the name and type for each column and creates the table. The ID is generated
        automatically for each entry.
     */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_NAME + " TEXT," +
                    COLUMN_NAME_SHORT_DESC + " TEXT," +
                    COLUMN_NAME_LONG_DESC + " TEXT," +
                    COLUMN_NAME_LATITUDE + " NUM," +
                    COLUMN_NAME_LONGITUDE + " NUM," +
                    COLUMN_NAME_ACTIVATION_BOX1 + " TEXT," +
                    COLUMN_NAME_ACTIVATION_BOX2 + " TEXT," +
                    COLUMN_NAME_ACTIVATION_BOX3 + " TEXT," +
                    COLUMN_NAME_ACTIVATION_BOX4 + " TEXT)" ;

    //Used to remove the table when needed
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public CUEDatabaseHelper(Context context) {
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
