package muhlenberg.edu.cue.services.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import muhlenberg.edu.cue.MainActivity;
import muhlenberg.edu.cue.util.location.CUELocation;

import static android.provider.BaseColumns._ID;


public class CUEDatabaseHelper extends SQLiteOpenHelper {
    /*
        Defines the name and type for each column and creates the table. The ID is generated
        automatically for each entry.
     */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CUEDatabaseContract.POI.TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CUEDatabaseContract.POI.COLUMN_NAME_NAME + " TEXT," +
                    CUEDatabaseContract.POI.COLUMN_NAME_SHORT_DESC + " TEXT," +
                    CUEDatabaseContract.POI.COLUMN_NAME_LONG_DESC + " TEXT," +
                    CUEDatabaseContract.POI.COLUMN_NAME_LATITUDE + " REAL," +
                    CUEDatabaseContract.POI.COLUMN_NAME_LONGITUDE + " REAL," +
                    CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX1 + " TEXT," +
                    CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX2 + " TEXT," +
                    CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX3 + " TEXT," +
                    CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX4 + " TEXT)" ;

    private static final String SQL_CREATE_TOUR_TABLE =
            "CREATE TABLE " + CUEDatabaseContract.Tour.TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    CUEDatabaseContract.Tour.COLUMN_NAME_NAME + " TEXT)";

    private static final String SQL_CREATE_POINT_TABLE =
            "CREATE TABLE " + CUEDatabaseContract.Point.TABLE_NAME + " (" +
                    CUEDatabaseContract.Point.COLUMN_NAME_TOUR_ID + " INTEGER," +
                    CUEDatabaseContract.Point.COLUMN_NAME_ORDER_NUMBER + " INTEGER," +
                    CUEDatabaseContract.Point.COLUMN_NAME_LATITUDE + " TEXT," +
                    CUEDatabaseContract.Point.COLUMN_NAME_LONGITUDE + " TEXT," +
                        "FOREIGN KEY (TOUR_ID) REFERENCES TOUR(_ID))";


    //Used to remove the table when needed
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CUEDatabaseContract.POI.TABLE_NAME;

    // Main Activity Context
    Context context;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public CUEDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Creates table and inserts default data on first time opening the app
     * @param db (instance of the database)
     */
    public void onCreate(SQLiteDatabase db) {
        // creates tables within database
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_TOUR_TABLE);
        db.execSQL(SQL_CREATE_POINT_TABLE);

        // inserts buildings into the database
        Building[] buildings = new Building[6];

        // creates building objects to be stored in an array
        buildings[0] = new Building(-1, "Trumbower", "Building", "Math and Science", 40.597450f, -75.510855f);
        buildings[1] = new Building(-1, "Hass", "Building", "College Offices", 40.597629f, -75.510136f);
        buildings[2] = new Building(-1, "New Science", "Building", "New Science Building", 40.597207f, -75.511698f);
        buildings[3] = new Building(-1, "Ettinger", "Building", "Business and History", 40.597804f, -75.509426f);
        buildings[4] = new Building(-1, "Moyer", "Building", "Useless Majors", 40.597930f, -75.508640f);
        buildings[5] = new Building(-1, "Life Sports Center", "Building", "Gym", 40.599069f, -75.509579f);

        for(int i=0; i<buildings.length; i++){
            insertPOI(buildings[i], db);
        }
        // inserts tours into the database
        List<CUELocation> pointList = readPointList("TestTour");
        Tour testTour = new Tour("TestTour", 0, pointList);
        insertTourTable(testTour, db);
        insertPointTable(testTour, db);
    }

    private long insertPOI(Building b, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(CUEDatabaseContract.POI.COLUMN_NAME_NAME, b.getName());
        values.put(CUEDatabaseContract.POI.COLUMN_NAME_SHORT_DESC, b.getShortDesc());
        values.put(CUEDatabaseContract.POI.COLUMN_NAME_LONG_DESC, b.getLongDesc());
        values.put(CUEDatabaseContract.POI.COLUMN_NAME_LATITUDE, b.getLat());
        values.put(CUEDatabaseContract.POI.COLUMN_NAME_LONGITUDE, b.getLng());
        values.put(CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX1, b.getActivationBoxNE().toString());
        values.put(CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX2, b.getActivationBoxNW().toString());
        values.put(CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX3, b.getActivationBoxSE().toString());
        values.put(CUEDatabaseContract.POI.COLUMN_NAME_ACTIVATION_BOX4, b.getActivationBoxSW().toString());

        //Inserts the POI and returns the ID
        return db.insert(CUEDatabaseContract.POI.TABLE_NAME, null, values);
    }

    /*
    inserts tour's name and into the database
     */
    private long insertTourTable(Tour tour, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(CUEDatabaseContract.Tour.COLUMN_NAME_NAME, tour.getName());
        return db.insert(CUEDatabaseContract.Tour.TABLE_NAME, null, values);
    }

    /*
    inserts the corresponding tour's pointlist into the database
     */
    private void insertPointTable(Tour tour, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        for(int i=0; i<tour.getPointList().size(); i++){
            values.put(CUEDatabaseContract.Point.COLUMN_NAME_ORDER_NUMBER, i);
            values.put(CUEDatabaseContract.Point.COLUMN_NAME_LATITUDE, tour.getPointList().get(i).getLatitude());
            values.put(CUEDatabaseContract.Point.COLUMN_NAME_LONGITUDE, tour.getPointList().get(i).getLongitude());
            db.insert(CUEDatabaseContract.Point.TABLE_NAME, null, values);
        }
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

    /*
    Reads the file of points from an .txt file in the asset folder
 */
    public List<CUELocation> readPointList(String fileName){
        BufferedReader reader = null;
        List<CUELocation> pointList = new ArrayList<CUELocation>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(this.context.getAssets().open("TestTour.txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                String[] latlng = mLine.split(",");
                CUELocation loc = new CUELocation(Double.parseDouble(latlng[1]), Double.parseDouble(latlng[0]));
                pointList.add(loc);
            }
        } catch (IOException e) {
            Log.d("FileNotOpened", "File was not opened correctly");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d("FileNotClosed", "File was not closed correctly");
                }
            }
        }
        return pointList;
    }
}
