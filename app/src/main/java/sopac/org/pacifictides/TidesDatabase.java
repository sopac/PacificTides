package sopac.org.pacifictides;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by sachin on 1/30/17.
 */

public class TidesDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "tides.db";
    private static final int DATABASE_VERSION = 1;

    public TidesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

}
