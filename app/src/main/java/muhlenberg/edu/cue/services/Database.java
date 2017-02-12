package muhlenberg.edu.cue.services;

/**
 * Created by jason on 2/7/17.
 */

import android.content.ContentValues;
import android.database.sqlite.*;

import muhlenberg.edu.cue.services.FeedReaderContract;
import muhlenberg.edu.cue.services.FeedReaderDbHelper;

import static muhlenberg.edu.cue.ARSimpleApplication.getContext;

public class Database {

    public Database() {


    }
    public void POI() {

        /**
         *  Format:
         *  insertPOI(name, short desc, long desc, latitude, longitude, activation range);
         */

        insertPOI("test", "test", "test", 1, 1, 1);

    }

        public long insertPOI(String name, String shortDesc, String longDesc, float latitude, float longitude, float activationRange) {
            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());
            SQLiteDatabase sql = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SHORT_DESC, shortDesc);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LONG_DESC, longDesc);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE, latitude);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE, longitude);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ACTIVATION_RANGE, activationRange);

            //Inserts the POI and returns the ID
            long newRowId = sql.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

            return newRowId;
        }

}
