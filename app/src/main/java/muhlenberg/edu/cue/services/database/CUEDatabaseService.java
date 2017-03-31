package muhlenberg.edu.cue.services.database;

/**
 * Created by jason on 2/7/17.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import muhlenberg.edu.cue.MainActivity;
import muhlenberg.edu.cue.services.AbstractService;


public class CUEDatabaseService extends AbstractService {

    private static CUEDatabaseService instance;
    private static SQLiteDatabase sql;
    private static CUEDatabaseHelper mDbHelper;

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
    public static CUEDatabaseService getInstance() {
        if (instance == null) {
            instance = new CUEDatabaseService();
        }

        return instance;
    }

    // Unused constructor
    public CUEDatabaseService() {

    }

    public Building[] readAllPOI () {
        String selectQuery = "SELECT * FROM " + CUEDatabaseContract.POI.TABLE_NAME;
        Cursor cursor = sql.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        Building[] buildings = new Building[cursor.getCount()];
        for(int i=0; i<buildings.length; i++) {
            buildings[i] = new Building(cursor.getInt(ID),
                    cursor.getString(NAME),
                    cursor.getString(SHORT_DESC),
                    cursor.getString(LONG_DESC),
                    cursor.getFloat(LATITUDE),
                    cursor.getFloat(LONGITUDE));
            cursor.moveToNext();
        }
        cursor.close();

        return buildings;
    }

    /*
        This will allow things from the database to be read
        INCOMPLETE
     */
    public Building readPOI(String name) {
        // The rows that will be read (which is every row in this case)
        String[] projection = {
                CUEDatabaseContract.POI._ID,
                CUEDatabaseContract.POI.COLUMN_NAME_NAME,
                CUEDatabaseContract.POI.COLUMN_NAME_SHORT_DESC,
                CUEDatabaseContract.POI.COLUMN_NAME_LONG_DESC,
                CUEDatabaseContract.POI.COLUMN_NAME_LATITUDE,
                CUEDatabaseContract.POI.COLUMN_NAME_LONGITUDE,
                CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX1,
                CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX2,
                CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX3,
                CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX4
        };

        // Find the correct row based off the name of the POI
        String selection = CUEDatabaseContract.POI.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { name };

        // Reads from the database into cursor, using the table and the selected columns
        // and gets all the data from the given name
        Cursor cursor = sql.query(
                CUEDatabaseContract.POI.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // creates a building out of the query to the db and returns it
        Building building = new Building(cursor.getInt(ID),           // Gets building id
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
//        CUELocation NE = new CUELocation(cursor.getString(ACTIVATION_BOX_NE));
//        CUELocation NW = new CUELocation(cursor.getString(ACTIVATION_BOX_NW));
//        CUELocation SE = new CUELocation(cursor.getString(ACTIVATION_BOX_SE));
//        CUELocation SW = new CUELocation(cursor.getString(ACTIVATION_BOX_SW));
//
//        building.addActivationBoxCoordinate(NE, NW, SE, SW);

        cursor.close();


        return building;
    }

    /*
        Opens the writer to begin writing to the database.
     */
    @Override
    public void start(Context context) {
        if(mDbHelper == null && sql == null) {
            mDbHelper = new CUEDatabaseHelper(context);
            sql = mDbHelper.getWritableDatabase();
        }
    }

    /*
        Closes the writer.
     */
    @Override
    public void stop(Context context) {
        sql.close();
        mDbHelper.close();

        mDbHelper = null;
        sql = null;
    }
}
