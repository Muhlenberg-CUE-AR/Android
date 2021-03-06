package muhlenberg.edu.cue.services.database;


import android.provider.BaseColumns;

/**
 * Created by jason on 2/7/17.
 */
public final class CUEDatabaseContract {



    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CUEDatabaseContract() {
    }

    /* Inner class that defines the table contents */
    public static class POI implements BaseColumns {
        public static final String TABLE_NAME = "POI";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_SHORT_DESC = "ShortDesc";
        public static final String COLUMN_NAME_LONG_DESC = "LongDesc";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_ACTIVATION_BOX1 = "ActivationBox1";
        public static final String COLUMN_NAME_ACTIVATION_BOX2 = "ActivationBox2";
        public static final String COLUMN_NAME_ACTIVATION_BOX3 = "ActivationBox3";
        public static final String COLUMN_NAME_ACTIVATION_BOX4 = "ActivationBox4";

    }

    public static class Tour implements BaseColumns {
        public static final String TABLE_NAME = "Tour";
        public static final String COLUMN_NAME_NAME = "Name";
    }

    public static class Point implements BaseColumns {
        public static final String TABLE_NAME = "Point";
        public static final String COLUMN_NAME_TOUR_ID = "TOUR_ID";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_ORDER_NUMBER = "OrderNumber";
    }
}


