package muhlenberg.edu.cue.util.text;


import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.*;

import android.provider.BaseColumns;

import static muhlenberg.edu.cue.ARSimpleApplication.getContext;
import static muhlenberg.edu.cue.util.text.FeedReaderContract.FeedEntry.*;

/**
 * Created by jason on 2/7/17.
 */
public final class FeedReaderContract {

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

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "POI";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_SHORT_DESC = "ShortDesc";
        public static final String COLUMN_NAME_LONG_DESC = "LongDesc";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_ACTIVATION_RANGE = "ActivationRange";

    }

    public class FeedReaderDbHelper extends SQLiteOpenHelper {
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

    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());

    public long insertPOI(String name, String shortDesc, String longDesc, float latitude, float longitude, float activationRange) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(FeedEntry.COLUMN_NAME_NAME, name);
        values.put(FeedEntry.COLUMN_NAME_SHORT_DESC, shortDesc);
        values.put(FeedEntry.COLUMN_NAME_LONG_DESC, longDesc);
        values.put(FeedEntry.COLUMN_NAME_LONGITUDE, longitude);
        values.put(FeedEntry.COLUMN_NAME_LATITUDE, latitude);
        values.put(FeedEntry.COLUMN_NAME_ACTIVATION_RANGE, activationRange);

        //Inserts the POI and returns the ID
        long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);

        return newRowId;
    }

}
