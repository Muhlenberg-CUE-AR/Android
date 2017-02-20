package muhlenberg.edu.cue.services;

/**
 * Created by jason on 2/7/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import static muhlenberg.edu.cue.ARSimpleApplication.getContext;

public class CUEDatabaseService extends AbstractService {

    private static CUEDatabaseService instance;
    private SQLiteDatabase sql;
    private CUEDatabaseHelper mDbHelper;

    // Creates an instance of the database
    public CUEDatabaseService getInstance() {
        if (instance == null) {
            instance = new CUEDatabaseService();
        }

        return instance;
    }

    // Unused constructor
    public CUEDatabaseService() {

    }

    /*
        Used to enter in all POIs. Calling this function adds them all to the database.
     */
    public void createAllPOIs() {

        /**
         *  Format:
         *  insertPOI(name, short desc, long desc, latitude, longitude, activation box coordinates 1-4);
         */

        insertPOI("test", "test", "test", 1, 1, 1, 1, 1, 1);
        insertPOI("test", "test", "test", 1, 1, 1, 1, 1, 1);
        insertPOI("test", "test", "test", 1, 1, 1, 1, 1, 1);
        insertPOI("test", "test", "test", 1, 1, 1, 1, 1, 1);

    }

    /*
        Adds a row to the database. Uses the variable values to store each column in the row and
        then inserting it into the table.
     */
    public long insertPOI(String name, String shortDesc, String longDesc, float latitude, float longitude,
                          float activationBox1, float activationBox2, float activationBox3, float activationBox4) {

        ContentValues values = new ContentValues();
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_NAME, name);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_SHORT_DESC, shortDesc);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONG_DESC, longDesc);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LATITUDE, latitude);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONGITUDE, longitude);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX1, activationBox1);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX2, activationBox2);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX3, activationBox3);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX4, activationBox4);

        //Inserts the POI and returns the ID
        long newRowId = sql.insert(CUEDatabaseContract.FeedEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    /*
        This will allow things from the database to be read
        INCOMPLETE
     */
    public Cursor readPOI(String name) {
        this.sql = mDbHelper.getReadableDatabase();

        // The rows that will be read (which is every row in this case)
        String[] projection = {
                CUEDatabaseContract.FeedEntry._ID,
                CUEDatabaseContract.FeedEntry.COLUMN_NAME_NAME,
                CUEDatabaseContract.FeedEntry.COLUMN_NAME_SHORT_DESC,
                CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONG_DESC,
                CUEDatabaseContract.FeedEntry.COLUMN_NAME_LATITUDE,
                CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONGITUDE,
                CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX1,
                CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX2,
                CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX3,
                CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX4
        };

        // Find the correct row based off the name of the POI
        String selection = CUEDatabaseContract.FeedEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { name };

        // Reads from the database into cursor, using the table and the selected columns
        // and gets all the data from the given name
        Cursor cursor = this.sql.query(
                CUEDatabaseContract.FeedEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // cursor needs to close, this may need to be changed.
        return cursor;
    }

    /*
        Opens the writer to begin writing to the database.
     */
    @Override
    public void start(Context context) {
        this.mDbHelper = new CUEDatabaseHelper(getContext());
        this.sql = mDbHelper.getWritableDatabase();

        mDbHelper.close();
    }

    /*
        Closes the writer.
     */
    @Override
    public void stop(Context context) {
        this.sql.close();
        this.mDbHelper.close();
    }
}
