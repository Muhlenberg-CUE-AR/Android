package muhlenberg.edu.cue.services;


import android.provider.BaseColumns;

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


