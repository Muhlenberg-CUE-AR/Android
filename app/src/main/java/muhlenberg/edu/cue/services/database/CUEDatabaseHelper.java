package muhlenberg.edu.cue.services.database;

import android.content.ContentValues;
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
                    COLUMN_NAME_LATITUDE + " REAL," +
                    COLUMN_NAME_LONGITUDE + " REAL," +
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

    /**
     * Creates table and inserts default data on first time opening the app
     * @param db
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

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
    }

    private long insertPOI(Building b, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_NAME, b.getName());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_SHORT_DESC, b.getShortDesc());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONG_DESC, b.getLongDesc());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LATITUDE, b.getLat());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONGITUDE, b.getLng());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX1, b.getActivationBoxNE().toString());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX2, b.getActivationBoxNW().toString());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX3, b.getActivationBoxSE().toString());
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_BOX4, b.getActivationBoxSW().toString());

        //Inserts the POI and returns the ID
        return db.insert(CUEDatabaseContract.FeedEntry.TABLE_NAME, null, values);
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
