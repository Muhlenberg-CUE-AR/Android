package muhlenberg.edu.cue.services;

/**
 * Created by jason on 2/7/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import muhlenberg.edu.cue.util.location.CUELocation;

import static muhlenberg.edu.cue.ARSimpleApplication.getContext;

public class CUEDatabaseService extends AbstractService {

    private static CUEDatabaseService instance;
    private SQLiteDatabase sql;
    private CUEDatabaseHelper mDbHelper;

    // Constants for retrieving information from the database
    public final static int ID = 0;
    public final static int NAME = 1;
    public final static int SHORT_DESC = 2;
    public final static int LONG_DESC = 3;
    public final static int LATITUDE = 4;
    public final static int LONGITUDE = 5;
    public final static int ACTIVATION_BOX_NE = 6;
    public final static int ACTIVATION_BOX_NW = 7;
    public final static int ACTIVATION_BOX_SE = 8;
    public final static int ACTIVATION_BOX_SW = 9;

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
         *  insertPOI( id, name, short desc, long desc, latitude, longitude, activation box coordinates 1-4);
         */

        insertPOI("Trumbower", "Building", "Math and Science", 1, 1, "1.0, 1.0", "1.0, 1.0", "1.0, 1.0", "1.0, 1.0");
        // insertPOI("test", "test", "test", 1, 1, 1, 1, 1, 1);
        // insertPOI("test", "test", "test", 1, 1, 1, 1, 1, 1);
        // insertPOI("test", "test", "test", 1, 1, 1, 1, 1, 1);

    }

    /*
        Adds a row to the database. Uses the variable values to store each column in the row and
        then inserting it into the table.
     */
    public long insertPOI(String name, String shortDesc, String longDesc, float latitude, float longitude,
                          String activationBox1, String activationBox2, String activationBox3, String activationBox4) {

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
    public Building readPOI(String name) {
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

        // creates a building out of the query to the db and returns it

        Building b1 = new Building(cursor.getInt(ID),           // Gets building id
                                   cursor.getString(NAME),      // Gets building name
                                   cursor.getString(SHORT_DESC),// Gets building short description
                                   cursor.getString(LONG_DESC), // Gets building long description
                                   cursor.getFloat(LATITUDE),   // Gets building latitude
                                   cursor.getFloat(LONGITUDE));  // Gets building longitude
                                   /*cursor.getString(ACTIVATION_BOX_NE), // Gets the activation boxes as string
                                   cursor.getString(ACTIVATION_BOX_NW),
                                   cursor.getString(ACTIVATION_BOX_SE),
                                   cursor.getString(ACTIVATION_BOX_SW));*/

        // uses the string to get a lat and lon for the location
        CUELocation NE = new CUELocation(cursor.getString(ACTIVATION_BOX_NE));
        CUELocation NW = new CUELocation(cursor.getString(ACTIVATION_BOX_NW));
        CUELocation SE = new CUELocation(cursor.getString(ACTIVATION_BOX_SE));
        CUELocation SW = new CUELocation(cursor.getString(ACTIVATION_BOX_SW));

        b1.addActivationBoxCoordinate(NE, NW, SE, SW);

        cursor.close();
        return b1;
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
