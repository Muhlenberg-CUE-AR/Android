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



    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {
    }

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

}


