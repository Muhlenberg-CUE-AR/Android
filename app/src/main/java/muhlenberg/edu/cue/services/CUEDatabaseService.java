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
        // array of all of the buildings we plan on including
        Building[] buildings = new Building[24];

        // creates building objects to be stored in an array
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", 40.597450f, -75.510855f);
        Trumbower.addActivationBoxCoordinate(new CUELocation(40.598091, -75.509107),
                new CUELocation(40.598297, -75.508383),
                new CUELocation(40.597533, -75.508823),
                null);
        buildings[0] = Trumbower;
        Building Haas = new Building(1, "Hass", "Building", "College Offices", 40.597629f, -75.510136f);
        buildings[1] = Haas;
        Building New_Sci = new Building(2, "New Science", "Building", "New Science Building", 40.597207f, -75.511698f);
        buildings[2] = New_Sci;
        Building Ettinger = new Building(3, "Ettinger", "Building", "Business and History", 40.597804f, -75.509426f);
        Ettinger.addActivationBoxCoordinate(new CUELocation(40.597935, -75.509952),
                new CUELocation(40.598092, -75.509112),
                new CUELocation(40.597538, -75.509732),
                null);
        buildings[3] = Ettinger;
        Building Moyer = new Building(4, "Moyer", "Building", "Useless Majors", 40.597930f, -75.508640f);
        buildings[4] = Moyer;
        /*Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);
        Building Trumbower = new Building(0, "Trumbower", "Building", "Math and Science", -40.59f, -75.51f);*/

        insertAllPOI(buildings);

    }

    /*
        Adds a row to the database. Uses the variable values to store each column in the row and
        then inserting it into the table.
     */
    public long insertPOI(Building b) {

        ContentValues values = new ContentValues();
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_NAME, b.getName());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_SHORT_DESC, b.getShortDesc());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONG_DESC, b.getLongDesc());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LATITUDE, b.getLat());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONGITUDE, b.getLon());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX1, b.getActivationBoxNE().toString());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX2, b.getActivationBoxNW().toString());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX3, b.getActivationBoxSE().toString());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX4, b.getActivationBoxSW().toString());

        //Inserts the POI and returns the ID
        long newRowId = sql.insert(CUEDatabaseContract.FeedEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    /*
        Adds all points of interest to the database
     */
    public void insertAllPOI(Building[] buildings) {
        for(int i=0; i<buildings.length; i++){
            insertPOI(buildings[i]);
        }
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
