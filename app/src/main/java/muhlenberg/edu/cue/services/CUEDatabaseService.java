package muhlenberg.edu.cue.services;

/**
 * Created by jason on 2/7/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.*;

import static muhlenberg.edu.cue.ARSimpleApplication.getContext;

public class CUEDatabaseService extends AbstractService {

    private static CUEDatabaseService instance;
    private SQLiteDatabase sql;
    private CUEDatabaseHelper mDbHelper;

    public CUEDatabaseService getInstance() {
        if (instance == null) {
            instance = new CUEDatabaseService();
        }

        return instance;
    }


    public CUEDatabaseService() {

    }

    public void createAllPOIs() {

        /**
         *  Format:
         *  insertPOI(name, short desc, long desc, latitude, longitude, activation range);
         */

        insertPOI("test", "test", "test", 1, 1, 1);
        insertPOI("test", "test", "test", 1, 1, 1);
        insertPOI("test", "test", "test", 1, 1, 1);
        insertPOI("test", "test", "test", 1, 1, 1);

    }

    public long insertPOI(String name, String shortDesc, String longDesc, float latitude, float longitude, float activationRange) {

        ContentValues values = new ContentValues();
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_NAME, name);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_SHORT_DESC, shortDesc);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONG_DESC, longDesc);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LATITUDE, latitude);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_LONGITUDE, longitude);
        values.put(CUEDatabaseContract.FeedEntry.COLUMN_NAME_ACTIVATION_RANGE, activationRange);

        //Inserts the POI and returns the ID
        long newRowId = sql.insert(CUEDatabaseContract.FeedEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    @Override
    public void start(Context context) {
        this.mDbHelper = new CUEDatabaseHelper(getContext());
        this.sql = mDbHelper.getWritableDatabase();

        mDbHelper.close();
    }

    @Override
    public void stop(Context context) {
        this.sql.close();
        this.mDbHelper.close();
    }
}
